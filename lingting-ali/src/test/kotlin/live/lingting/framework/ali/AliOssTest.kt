package live.lingting.framework.ali

import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.util.function.Consumer
import live.lingting.framework.ali.exception.AliException
import live.lingting.framework.ali.properties.AliOssProperties
import live.lingting.framework.aws.s3.AwsS3MultipartTask
import live.lingting.framework.aws.s3.AwsS3Utils
import live.lingting.framework.aws.s3.request.AwsS3SimpleRequest
import live.lingting.framework.aws.s3.response.AwsS3MultipartItem
import live.lingting.framework.http.download.HttpDownload
import live.lingting.framework.http.download.HttpDownload.single
import live.lingting.framework.id.Snowflake
import live.lingting.framework.thread.Async
import live.lingting.framework.util.CollectionUtils
import live.lingting.framework.util.DigestUtils
import live.lingting.framework.util.StreamUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author lingting 2024-09-18 14:29
 */
@EnabledIfSystemProperty(named = "framework.ali.oss.test", matches = "true")
internal class AliOssTest {
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
        Assertions.assertThrows(AliException::class.java) { ossObject.head() }
        val source = "hello world"
        val bytes = source.toByteArray()
        val hex = DigestUtils.md5Hex(bytes)
        Assertions.assertDoesNotThrow { ossObject.put(ByteArrayInputStream(bytes)) }
        val head = ossObject.head()
        Assertions.assertNotNull(head)
        Assertions.assertEquals(bytes.size.toLong(), head.contentLength())
        Assertions.assertTrue("\"%s\"".formatted(hex).equals(head.etag(), ignoreCase = true))
        val await: HttpDownload = single(ossObject.publicUrl()!!).build().start().await()
        val string = StreamUtils.toString(Files.newInputStream(await.getFile().toPath()))
        Assertions.assertEquals(source, string)
        ossObject.delete()
    }


    @Test
    fun multipart() {
        val ossBucket = sts!!.ossBucket(properties!!)
        val bo = ossBucket.use("ali/b_t")
        val uploadId = bo.multipartInit()
        val bm = ossBucket.multipartList { r: AwsS3SimpleRequest? ->
            val params = r!!.params
            params.add("prefix", bo.key!!)
        }
        Assertions.assertFalse(bm!!.isEmpty())
        Assertions.assertTrue(bm.stream().anyMatch { i: AwsS3MultipartItem? -> i!!.key == bo.key })
        Assertions.assertTrue(bm.stream().anyMatch { i: AwsS3MultipartItem? -> i!!.uploadId == uploadId })

        val list = ossBucket.multipartList()
        if (!CollectionUtils.isEmpty(list)) {
            list!!.forEach(Consumer { i: AwsS3MultipartItem? ->
                val ossObject = ossBucket.use(i!!.key)
                ossObject.multipartCancel(i.uploadId)
            })
        }

        val snowflake = Snowflake(0, 1)
        val key = "ali/m_" + snowflake.nextId()
        val ossObject = sts!!.ossObject(properties!!, key)
        Assertions.assertThrows(AliException::class.java) { ossObject.head() }
        val source = "hello world\n".repeat(10000)
        val bytes = source.toByteArray()
        val hex = DigestUtils.md5Hex(bytes)
        val task = Assertions.assertDoesNotThrow<AwsS3MultipartTask> { ossObject.multipart(ByteArrayInputStream(bytes), 1, Async(10)) }
        Assertions.assertTrue(task.isStarted)
        task.await()
        if (task.hasFailed()) {
            for (t in task.tasksFailed()) {
                log.error("multipart error!", t.t)
            }
        }
        Assertions.assertTrue(task.isCompleted)
        Assertions.assertFalse(task.hasFailed())
        val multipart = task.multipart
        Assertions.assertTrue(multipart.partSize >= AwsS3Utils.MULTIPART_MIN_PART_SIZE)
        val head = ossObject.head()
        Assertions.assertNotNull(head)
        Assertions.assertEquals(bytes.size.toLong(), head.contentLength())
        val await: HttpDownload = single(ossObject.publicUrl()!!).build().start().await()
        val string = StreamUtils.toString(Files.newInputStream(await.getFile().toPath()))
        Assertions.assertEquals(source, string)
        assertEquals(hex, DigestUtils.md5Hex(string))
        ossObject.delete()
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(AliOssTest::class.java)
    }
}
