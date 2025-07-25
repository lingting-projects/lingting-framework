package live.lingting.framework.huawei

import live.lingting.framework.aws.AwsUtils.MULTIPART_MIN_PART_SIZE
import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.s3.AwsS3Meta
import live.lingting.framework.aws.s3.impl.S3Meta
import live.lingting.framework.concurrent.Await
import live.lingting.framework.http.HttpClient
import live.lingting.framework.http.HttpRequest
import live.lingting.framework.http.download.HttpDownload
import live.lingting.framework.huawei.exception.HuaweiException
import live.lingting.framework.huawei.properties.HuaweiObsProperties
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
import org.junit.jupiter.api.function.ThrowingSupplier
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.util.function.Consumer

/**
 * @author lingting 2024-09-13 17:13
 */
@EnabledIfSystemProperty(named = "framework.huawei.obs.test", matches = "true")
class HuaweiObsTest {

    private val log = logger()

    var iam: HuaweiIam? = null

    var properties: HuaweiObsProperties? = null

    val snowflake = Snowflake(0, 1)

    @BeforeEach
    fun before() {
        iam = HuaweiBasic.iam()
        properties = HuaweiBasic.obsProperties()
    }

    @Test
    fun put() {
        val snowflake = Snowflake(0, 0)
        val key = "test/" + snowflake.nextId()
        val obsObject = iam!!.obsObject(properties!!, key)
        assertThrows(HuaweiException::class.java) { obsObject.head() }
        val source = "hello world"
        val bytes = source.toByteArray()
        val hex = DigestUtils.md5Hex(bytes)
        assertDoesNotThrow { obsObject.put(ByteArrayInputStream(bytes)) }
        val head = obsObject.head()
        assertNotNull(head)
        assertEquals(bytes.size.toLong(), head.contentLength())
        assertEquals("\"$hex\"", head.etag())
        val await = HttpDownload.single(obsObject.publicUrl()).build().start().await()
        val string = StreamUtils.toString(Files.newInputStream(await.file.toPath()))
        assertEquals(source, string)
        obsObject.delete()
    }

    @Test
    fun multipart() {
        val obsBucket = iam!!.obsBucket(properties!!)
        val items = obsBucket.multipartList()
        if (items.isNotEmpty()) {
            items.forEach(Consumer { item ->
                val k = item.key
                val v = item.uploadId
                val obsObject = obsBucket.use(k)
                obsObject.multipartCancel(v)
            })
        }

        val key = "huawei/obs/test/" + snowflake.nextId()
        val obsObject = iam!!.obsObject(properties!!, key)
        assertThrows(HuaweiException::class.java) { obsObject.head() }
        val source = "hello world".repeat(90000)
        val bytes = source.toByteArray()
        val hex = DigestUtils.md5Hex(bytes)
        val task = assertDoesNotThrow(ThrowingSupplier {
            obsObject.multipart(
                ByteArrayInputStream(bytes),
                1.bytes,
                Async(10)
            )
        })
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
        assertTrue(multipart.partSize >= MULTIPART_MIN_PART_SIZE)
        val head = obsObject.head()
        assertNotNull(head)
        assertEquals(bytes.size.toLong(), head.contentLength())
        assertEquals(task.uploadId, head.multipartUploadId())
        val await = HttpDownload.single(obsObject.publicUrl()).build().start().await()
        val string = StreamUtils.toString(Files.newInputStream(await.file.toPath()))
        assertEquals(source, string)
        assertEquals(hex, DigestUtils.md5Hex(string))
        obsObject.delete()
    }

    @Test
    fun listAndMeta() {
        val obsBucket = iam!!.obsBucket(properties!!)
        val key = "test/b_t_l_m"
        val bo = obsBucket.use(key)
        val source = "hello world"
        val bytes = source.toByteArray()
        val md5 = DigestUtils.md5Hex(bytes)
        val meta = AwsS3Meta()
        meta.add("md5", md5)
        meta.add("timestamp", DateTime.millis().toString())
        bo.put(ByteArrayInputStream(bytes), meta)

        val lo = obsBucket.listObjects(key.substring(0, 4))
        assertTrue { lo.keyCount > 0 }
        val o = lo.contents?.any { it.key == key }
        assertNotNull(o)
        val lo2 = obsBucket.listObjects(key + "_21")
        assertTrue { lo2.keyCount == 0 }
        val o2 = lo2.contents?.any { it.key == key }
        assertNull(o2)

        val head = bo.head()
        assertNotNull(head)
        assertEquals(bytes.size.toLong(), head.contentLength())
        assertEquals(md5, head.first("md5"))
        assertEquals(meta.first("timestamp"), head.first("timestamp"))
        val await = HttpDownload.single(bo.publicUrl()).build().start().await()
        val string = StreamUtils.toString(Files.newInputStream(await.file().toPath()))
        assertEquals(source, string)
        assertEquals(md5, DigestUtils.md5Hex(string))
        bo.delete()
    }

    @Test
    fun pre() {
        val client = HttpClient.default().disableSsl().build()
        val key = "test/" + snowflake.nextId()
        log.info("pre key: {}", key)
        val obj = iam!!.obsObject(properties!!, key)

        val source = "hello world"
        val bytes = source.toByteArray()
        val hex = DigestUtils.md5Hex(bytes)

        try {
            log.info("ak: {}", obj.ak)
            log.info("sk: {}", obj.sk)
            log.info("token: {}", obj.token)

            log.info("=================put=================")
            val prePutR = obj.prePut(Acl.PRIVATE, S3Meta.empty().also {
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
            log.info("get url: {}", preGet.url)
            client.request(
                HttpRequest.builder()
                    .get()
                    .url(preGet.url)
                    .build()
            ).use { getR ->
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
