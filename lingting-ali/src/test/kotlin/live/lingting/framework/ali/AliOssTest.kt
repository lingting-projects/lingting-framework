package live.lingting.framework.ali

import live.lingting.framework.ali.exception.AliException
import live.lingting.framework.ali.properties.AliOssProperties
import live.lingting.framework.aws.AwsUtils
import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.s3.AwsS3Meta
import live.lingting.framework.aws.s3.AwsS3MultipartTask
import live.lingting.framework.concurrent.Await
import live.lingting.framework.http.HttpClient
import live.lingting.framework.http.HttpRequest
import live.lingting.framework.http.download.HttpDownload
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.id.Snowflake
import live.lingting.framework.thread.Async
import live.lingting.framework.time.DateTime
import live.lingting.framework.util.DataSizeUtils.bytes
import live.lingting.framework.util.DigestUtils
import live.lingting.framework.util.DurationUtils.millis
import live.lingting.framework.util.Slf4jUtils.logger
import live.lingting.framework.util.StreamUtils
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.util.function.Consumer

/**
 * @author lingting 2024-09-18 14:29
 */
@EnabledIfSystemProperty(named = "framework.ali.oss.test", matches = "true")
class AliOssTest {

    companion object {
        private val log = logger()
    }

    var sts: AliSts? = null

    var properties: AliOssProperties? = null

    val snowflake = Snowflake(0, 0)

    @BeforeEach
    fun before() {
        sts = AliBasic.sts()
        properties = AliBasic.ossProperties()
    }

    @Test
    fun put() {
        val key = "test/s_" + snowflake.nextId()
        log.info("put key: {}", key)
        val ossObject = sts!!.ossObject(properties!!, key)
        assertThrows(AliException::class.java) { ossObject.head() }
        try {
            val source = "hello world"
            val bytes = source.toByteArray()
            val hex = DigestUtils.md5Hex(bytes)
            assertDoesNotThrow { ossObject.put(ByteArrayInputStream(bytes)) }
            val head = ossObject.head()
            assertNotNull(head)
            assertEquals(bytes.size.toLong(), head.contentLength())
            assertTrue("\"$hex\"".equals(head.etag(), ignoreCase = true))
            val await: HttpDownload = HttpDownload.single(ossObject.publicUrl()).build().start().await()
            val string = StreamUtils.toString(Files.newInputStream(await.file().toPath()))
            assertEquals(source, string)
        } finally {
            ossObject.delete()
        }
    }

    @Test
    fun multipart() {
        val ossBucket = sts!!.ossBucket(properties!!)
        val bo = ossBucket.use("ali/b_t")
        val uploadId = bo.multipartInit()
        val bm = ossBucket.multipartList {
            val params = it.params
            params.add("prefix", bo.key)
        }
        assertFalse(bm.isEmpty())
        assertTrue(bm.any { it.key == bo.key })
        assertTrue(bm.any { it.uploadId == uploadId })

        val list = ossBucket.multipartList()
        if (list.isNotEmpty()) {
            list.forEach(Consumer {
                val ossObject = ossBucket.use(it.key)
                ossObject.multipartCancel(it.uploadId)
            })
        }

        val snowflake = Snowflake(0, 1)
        val key = "ali/m_" + snowflake.nextId()
        val ossObject = sts!!.ossObject(properties!!, key)
        assertThrows(AliException::class.java) { ossObject.head() }
        val source = "hello world\n".repeat(10000)
        val bytes = source.toByteArray()
        val hex = DigestUtils.md5Hex(bytes)
        val task = assertDoesNotThrow<AwsS3MultipartTask> {
            ossObject.multipart(
                ByteArrayInputStream(bytes),
                1.bytes,
                Async(10)
            )
        }
        assertTrue(task.isStarted)
        task.await()
        if (task.hasFailed()) {
            for (t in task.tasksFailed()) {
                log.error("multipart error!", t.t)
            }
        }
        assertTrue(task.isCompleted)
        assertFalse(task.hasFailed())
        val multipart = task.multipart
        assertTrue(multipart.partSize >= AwsUtils.MULTIPART_MIN_PART_SIZE)
        val head = ossObject.head()
        assertNotNull(head)
        assertEquals(bytes.size.toLong(), head.contentLength())
        val await: HttpDownload = HttpDownload.single(ossObject.publicUrl()).build().start().await()
        val string = StreamUtils.toString(Files.newInputStream(await.file().toPath()))
        assertEquals(source, string)
        assertEquals(hex, DigestUtils.md5Hex(string))
        ossObject.delete()
    }

    @Test
    fun listAndMeta() {
        val ossBucket = sts!!.ossBucket(properties!!)
        val key = "ali/b_t_l_m"
        val bo = ossBucket.use(key)
        val source = "hello world"
        val bytes = source.toByteArray()
        val md5 = DigestUtils.md5Hex(bytes)
        val meta = AwsS3Meta()
        meta.add("md5", md5)
        meta.add("timestamp", DateTime.millis().toString())
        bo.put(ByteArrayInputStream(bytes), meta)

        val lo = ossBucket.listObjects(key.substring(0, 4))
        assertTrue { lo.keyCount > 0 }
        val o = lo.contents?.any { it.key == key }
        assertNotNull(o)
        val lo2 = ossBucket.listObjects(key + "_21")
        assertTrue { lo2.keyCount == 0 }
        val o2 = lo2.contents?.any { it.key == key }
        assertNull(o2)

        val head = bo.head()
        assertNotNull(head)
        assertEquals(bytes.size.toLong(), head.contentLength())
        assertEquals(md5, head.first("md5"))
        assertEquals(meta.first("timestamp"), head.first("timestamp"))
        val await: HttpDownload = HttpDownload.single(bo.publicUrl()).build().start().await()
        val string = StreamUtils.toString(Files.newInputStream(await.file().toPath()))
        assertEquals(source, string)
        assertEquals(md5, DigestUtils.md5Hex(string))
        bo.delete()
    }

    @Test
    fun pre() {
        val client = HttpClient.default().disableSsl().build()
        val key = "test/pre_" + snowflake.nextId()
        log.info("pre key: {}", key)
        val obj = sts!!.ossObject(properties!!, key)

        val source = "hello world"
        val bytes = source.toByteArray()
        val hex = DigestUtils.md5Hex(bytes)

        try {
            log.info("ak: {}", obj.ak)
            log.info("sk: {}", obj.sk)
            log.info("token: {}", obj.token)

            log.info("=================put=================")
            val prePutR = obj.prePut(Acl.PRIVATE, HttpHeaders.empty().also {
                it.put("pre", "true")
            })

            log.info("put url: {}", prePutR.url)

            client.request(
                HttpRequest.builder()
                    .put()
                    .body(source)
                    .url(prePutR.url)
                    .headers(prePutR.headers)
                    .build()
            ).use { putR ->
                assertTrue(putR.isOk)
            }

            Await.wait(500.millis)
            client.get(obj.publicUrl()).use { getR ->
                assertFalse(getR.isOk)
            }

            log.info("=================get=================")
            val preGet = obj.preGet()
            log.info("get url: {}", preGet)

            client.get(preGet).use { getR ->
                assertTrue(getR.isOk)
                val string = getR.string()
                assertEquals(source, string)
                assertEquals(hex, DigestUtils.md5Hex(string.toByteArray()))
            }

        } finally {
            log.info("=================delete=================")
            obj.delete()
        }
    }

}
