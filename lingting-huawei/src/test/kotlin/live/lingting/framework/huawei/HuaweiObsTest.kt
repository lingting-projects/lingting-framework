package live.lingting.framework.huawei

import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.util.function.Consumer
import live.lingting.framework.aws.s3.AwsS3MultipartTask
import live.lingting.framework.aws.s3.AwsS3Utils.MULTIPART_MIN_PART_SIZE
import live.lingting.framework.aws.s3.response.AwsS3MultipartItem
import live.lingting.framework.http.download.HttpDownload.Companion.single
import live.lingting.framework.huawei.exception.HuaweiException
import live.lingting.framework.huawei.properties.HuaweiObsProperties
import live.lingting.framework.id.Snowflake
import live.lingting.framework.thread.Async
import live.lingting.framework.util.DigestUtils.md5Hex
import live.lingting.framework.util.StreamUtils.toString
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.api.function.ThrowingSupplier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author lingting 2024-09-13 17:13
 */
@EnabledIfSystemProperty(named = "framework.huawei.obs.test", matches = "true")
internal class HuaweiObsTest {
    var iam: HuaweiIam? = null

    var properties: HuaweiObsProperties? = null

    @BeforeEach
    fun before() {
        iam = HuaweiBasic.iam()
        properties = HuaweiBasic.obsProperties()
    }

    @Test
    fun put() {
        val snowflake = Snowflake(0, 0)
        val key = "huawei/obs/test/" + snowflake.nextId()
        val obsObject = iam!!.obsObject(properties!!, key)
        assertThrows<HuaweiException>(HuaweiException::class.java, Executable { obsObject.head() })
        val source = "hello world"
        val bytes = source.toByteArray()
        val hex = md5Hex(bytes)
        assertDoesNotThrow(Executable { obsObject.put(ByteArrayInputStream(bytes)) })
        val head = obsObject.head()
        assertNotNull(head)
        assertEquals(bytes.size.toLong(), head.contentLength())
        assertEquals("\"$hex\"", head.etag())
        val await = single(obsObject.publicUrl()).build().start().await()
        val string = toString(Files.newInputStream(await.file.toPath()))
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

        val snowflake = Snowflake(0, 1)
        val key = "huawei/obs/test/" + snowflake.nextId()
        val obsObject = iam!!.obsObject(properties!!, key)
        assertThrows<HuaweiException>(HuaweiException::class.java, Executable { obsObject.head() })
        val source = "hello world".repeat(90000)
        val bytes = source.toByteArray()
        val hex = md5Hex(bytes)
        val task = assertDoesNotThrow<AwsS3MultipartTask>(
            ThrowingSupplier { obsObject.multipart(ByteArrayInputStream(bytes), 1, Async(10)) })
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
        val await = single(obsObject.publicUrl()).build().start().await()
        val string = toString(Files.newInputStream(await.file.toPath()))
        assertEquals(source, string)
        assertEquals(hex, md5Hex(string))
        obsObject.delete()
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(HuaweiObsTest::class.java)
    }
}
