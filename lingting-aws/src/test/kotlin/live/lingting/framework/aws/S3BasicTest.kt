package live.lingting.framework.aws

import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.properties.S3Properties
import live.lingting.framework.aws.s3.AwsS3Meta
import live.lingting.framework.aws.s3.AwsS3MultipartTask
import live.lingting.framework.aws.s3.impl.S3Meta
import live.lingting.framework.aws.s3.interfaces.AwsS3BucketDelegation
import live.lingting.framework.aws.s3.interfaces.AwsS3ObjectDelegation
import live.lingting.framework.concurrent.Await
import live.lingting.framework.http.HttpClient
import live.lingting.framework.http.HttpRequest
import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.download.HttpDownload
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
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Consumer

/**
 * @author lingting 2025/8/27 11:35
 */
abstract class S3BasicTest {

    private val log = logger()

    private val snowflake = Snowflake(0, 0)

    abstract fun buildObj(key: String): AwsS3ObjectDelegation

    abstract fun buildBucket(): AwsS3BucketDelegation

    abstract fun properties(): S3Properties

    protected val properties = properties()

    protected var client = HttpClient.default().disableSsl().build()

    fun run() {
        doTest()
        val domain = System.getenv("DOMAIN")
        if (!domain.isNullOrBlank()) {
            properties.domain = domain
            doTest()
        }
        val domain1 = System.getenv("DOMAIN1")
        if (!domain1.isNullOrBlank()) {
            properties.domain = domain1
            doTest()
        }
    }

    protected open fun doTest() {
        val async = Async()
        val atomic = AtomicLong()

        async.submit {
            try {
                put()
            } catch (t: Throwable) {
                atomic.incrementAndGet()
                log.error("[put] 测试异常! ", t)
            }
        }
        async.submit {
            try {
                multipart()
            } catch (t: Throwable) {
                atomic.incrementAndGet()
                log.error("[multipart] 测试异常! ", t)
            }
        }
        async.submit {
            try {
                pre()
            } catch (t: Throwable) {
                atomic.incrementAndGet()
                log.error("[pre] 测试异常! ", t)
            }
        }
        async.submit {
            try {
                listAndMeta()
            } catch (t: Throwable) {
                atomic.incrementAndGet()
                log.error("[listAndMeta] 测试异常! ", t)
            }
        }

        async.await()
        assertEquals(0, atomic.get())
    }

    protected open fun put() {
        val key = "test/p_" + snowflake.nextId()
        log.info("put key: {}", key)
        val obj = buildObj(key)
        assertThrows(RuntimeException::class.java) { obj.head() }
        try {
            val source = "hello world oss"
            val bytes = source.toByteArray()
            val hex = DigestUtils.md5Hex(bytes)
            assertDoesNotThrow { obj.put(ByteArrayInputStream(bytes), Acl.PUBLIC_READ) }
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

    protected open fun multipart() {
        val ossBucket = buildBucket()
        val bo = ossBucket.use("test/m_b_" + snowflake.nextId())
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
        val key = "test/m_" + snowflake.nextId()
        val ossObject = buildObj(key)
        try {
            assertThrows(RuntimeException::class.java) { ossObject.head() }
            val source = "hello world\n".repeat(10000)
            val bytes = source.toByteArray()
            val hex = DigestUtils.md5Hex(bytes)
            val task = assertDoesNotThrow<AwsS3MultipartTask> {
                ossObject.multipart(
                    ByteArrayInputStream(bytes),
                    1.bytes,
                    Async(10),
                    Acl.PUBLIC_READ
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
            val await = HttpDownload.single(ossObject.publicUrl()).build().start().await()
            val string = StreamUtils.toString(Files.newInputStream(await.file().toPath()))
            assertEquals(source, string)
            assertEquals(hex, DigestUtils.md5Hex(string))
        } finally {
            ossObject.delete()
        }
    }

    protected open fun listAndMeta() {
        val ossBucket = buildBucket()
        val key = "test/l_" + snowflake.nextId()
        val bo = ossBucket.use(key)
        try {
            val source = "hello world"
            val bytes = source.toByteArray()
            val md5 = DigestUtils.md5Hex(bytes)
            val meta = AwsS3Meta()
            meta.add("md5", md5)
            meta.add("timestamp", DateTime.millis().toString())
            bo.put(ByteArrayInputStream(bytes), Acl.PUBLIC_READ, meta)

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
            val await = HttpDownload.single(bo.publicUrl()).build().start().await()
            val string = StreamUtils.toString(Files.newInputStream(await.file().toPath()))
            assertEquals(source, string)
            assertEquals(md5, DigestUtils.md5Hex(string))
        } finally {
            bo.delete()
        }
    }

    protected open fun pre() {
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
            val prePutR = obj.prePut(Acl.PRIVATE, S3Meta.empty().also {
                it.put("pre", "true")
            })

            log.info("put url: {}", prePutR.url)

            client.request(
                HttpRequest.builder()
                    .put()
                    .body(source)
                    .url(HttpUrlBuilder.from(prePutR.url))
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
                    .url(HttpUrlBuilder.from(preGet.url))
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
