package live.lingting.framework.ali

import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.util.function.Consumer
import live.lingting.framework.ali.exception.AliException
import live.lingting.framework.ali.properties.AliOssProperties
import live.lingting.framework.aws.s3.AwsS3MultipartTask
import live.lingting.framework.aws.s3.AwsS3Utils
import live.lingting.framework.http.download.HttpDownload
import live.lingting.framework.id.Snowflake
import live.lingting.framework.thread.Async
import live.lingting.framework.util.DigestUtils
import live.lingting.framework.util.Slf4jUtils.logger
import live.lingting.framework.util.StreamUtils
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty

/**
 * @author lingting 2024-09-18 14:29
 */
@EnabledIfSystemProperty(named = "framework.ali.oss.test", matches = "true")
internal class AliOssTest {

    companion object {
        private val log = logger()
    }

    var sts: AliSts? = null

    var properties: AliOssProperties? = null

    @BeforeEach
    fun before() {
        sts = AliBasic.sts()
        properties = AliBasic.ossProperties()
    }

    @Test
    fun put() {
        val snowflake = Snowflake(0, 0)
        val key = "test/s_" + snowflake.nextId()
        log.info("key: {}", key)
        val ossObject = sts!!.ossObject(properties!!, key)
        assertThrows(AliException::class.java) { ossObject.head() }
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
        ossObject.delete()
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
        val task = assertDoesNotThrow<AwsS3MultipartTask> { ossObject.multipart(ByteArrayInputStream(bytes), 1, Async(10)) }
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
        assertTrue(multipart.partSize >= AwsS3Utils.MULTIPART_MIN_PART_SIZE)
        val head = ossObject.head()
        assertNotNull(head)
        assertEquals(bytes.size.toLong(), head.contentLength())
        val await: HttpDownload = HttpDownload.single(ossObject.publicUrl()).build().start().await()
        val string = StreamUtils.toString(Files.newInputStream(await.file().toPath()))
        assertEquals(source, string)
        assertEquals(hex, DigestUtils.md5Hex(string))
        ossObject.delete()
    }

}
