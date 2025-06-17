package live.lingting.framework.ali

import live.lingting.framework.aws.AwsBasic
import live.lingting.framework.aws.AwsS3Bucket
import live.lingting.framework.aws.AwsS3Object
import live.lingting.framework.aws.AwsSts
import live.lingting.framework.aws.AwsUtils
import live.lingting.framework.aws.exception.AwsException
import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.properties.AwsS3Properties
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
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Consumer

/**
 * @author lingting 2024-09-18 14:29
 */
@EnabledIfSystemProperty(named = "framework.aws.s3.test", matches = "true")
class AwsS3Test {

    private val log = logger()

    var sts: AwsSts? = null

    var properties: AwsS3Properties? = null

    val snowflake = Snowflake(0, 0)

    var useSts = false

    @BeforeEach
    fun before() {
        sts = AwsBasic.sts()
        properties = if (useSts) AwsBasic.s3StsProperties() else AwsBasic.s3Properties()
    }

    fun buildObj(key: String): AwsS3Object =
        if (useSts) sts!!.s3Object(properties!!, key) else AwsS3Object(AwsBasic.s3Properties(), key)

    fun buildBucket(): AwsS3Bucket =
        if (useSts) sts!!.s3Bucket(properties!!) else AwsS3Bucket(AwsBasic.s3Properties())

    @Test
    fun test() {
        doTest()
        val domain = System.getenv("DOMAIN")
        if (!domain.isNullOrBlank()) {
            properties!!.domain = domain
            doTest()
        }
        val domain1 = System.getenv("DOMAIN1")
        if (!domain1.isNullOrBlank()) {
            properties!!.domain = domain1
            doTest()
        }
    }

    fun doTest() {
        val async = Async()
        val atomic = AtomicLong()

        async.submit {
            try {
                put()
            } catch (_: Exception) {
                atomic.incrementAndGet()
            }
        }
        async.submit {
            try {
                multipart()
            } catch (_: Exception) {
                atomic.incrementAndGet()
            }
        }
        async.submit {
            try {
                pre()
            } catch (_: Exception) {
                atomic.incrementAndGet()
            }
        }
        async.submit {
            try {
                listAndMeta()
            } catch (_: Exception) {
                atomic.incrementAndGet()
            }
        }

        assertEquals(0, atomic.get())
        async.await()
    }

    fun put() {
        val key = "test/s_" + snowflake.nextId()
        log.info("put key: {}", key)
        val obj = buildObj(key)
        assertThrows(AwsException::class.java) { obj.head() }
        try {
            val source = "hello world s3"
            val bytes = source.toByteArray()
            val hex = DigestUtils.md5Hex(bytes)
            assertDoesNotThrow { obj.put(ByteArrayInputStream(bytes)) }
            val head = obj.head()
            assertNotNull(head)
            assertEquals(bytes.size.toLong(), head.contentLength())
            assertTrue("\"$hex\"".equals(head.etag(), ignoreCase = true))
            val await = HttpDownload.single(obj.publicUrl()).build().start().await()
            val string = StreamUtils.toString(Files.newInputStream(await.file().toPath()))
            assertEquals(source, string)
        } finally {
            obj.delete()
        }

    }

    fun multipart() {
        val s3Bucket = buildBucket()
        val bo = s3Bucket.use("test/b_t")
        val uploadId = bo.multipartInit()
        val bm = s3Bucket.multipartList {
            val params = it.params
            params.add("prefix", bo.key)
        }
        assertFalse(bm.isEmpty())
        assertTrue(bm.any { it.key == bo.key })
        assertTrue(bm.any { it.uploadId == uploadId })

        val list = s3Bucket.multipartList()
        if (list.isNotEmpty()) {
            list.forEach(Consumer {
                val s3Object = s3Bucket.use(it.key)
                s3Object.multipartCancel(it.uploadId)
            })
        }

        val snowflake = Snowflake(0, 1)
        val key = "test/m_" + snowflake.nextId()
        val s3Object = buildObj(key)
        assertThrows(AwsException::class.java) { s3Object.head() }
        val source = "hello world\n".repeat(10000)
        val bytes = source.toByteArray()
        val hex = DigestUtils.md5Hex(bytes)
        val task = assertDoesNotThrow<AwsS3MultipartTask> {
            s3Object.multipart(
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
        val head = s3Object.head()
        assertNotNull(head)
        assertEquals(bytes.size.toLong(), head.contentLength())
        val await = HttpDownload.single(s3Object.publicUrl()).build().start().await()
        val string = StreamUtils.toString(Files.newInputStream(await.file().toPath()))
        assertEquals(source, string)
        assertEquals(hex, DigestUtils.md5Hex(string))
        s3Object.delete()
    }

    fun listAndMeta() {
        val s3Bucket = buildBucket()
        val key = "test/b_t_l_m"
        val bo = s3Bucket.use(key)
        val source = "hello world"
        val bytes = source.toByteArray()
        val md5 = DigestUtils.md5Hex(bytes)
        val meta = AwsS3Meta()
        meta.add("md5", md5)
        meta.add("timestamp", DateTime.millis().toString())
        bo.put(ByteArrayInputStream(bytes), meta)

        val lo = s3Bucket.listObjects(key.substring(0, 4))
        assertTrue { lo.keyCount > 0 }
        val o = lo.contents?.any { it.key == key }
        assertNotNull(o)
        val lo2 = s3Bucket.listObjects(key + "_21")
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

    fun pre() {
        val client = HttpClient.default().disableSsl().build()
        val key = "test/pre_" + snowflake.nextId()
        log.info("pre key: {}", key)
        val obj = buildObj(key)

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
                if (!putR.isOk) {
                    println(putR.string())
                }
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
                val string = getR.string()
                if (!getR.isOk) {
                    println(string)
                }
                assertTrue(getR.isOk)
                assertEquals(source, string)
                assertEquals(hex, DigestUtils.md5Hex(string.toByteArray()))
            }

        } finally {
            log.info("=================delete=================")
            obj.delete()
        }
    }

}
