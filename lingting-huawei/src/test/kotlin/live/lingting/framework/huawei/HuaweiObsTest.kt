package live.lingting.framework.huawei

import live.lingting.framework.aws.s3.AwsS3Meta
import live.lingting.framework.aws.s3.AwsS3Utils.MULTIPART_MIN_PART_SIZE
import live.lingting.framework.http.download.HttpDownload
import live.lingting.framework.huawei.exception.HuaweiException
import live.lingting.framework.huawei.properties.HuaweiObsProperties
import live.lingting.framework.id.Snowflake
import live.lingting.framework.thread.Async
import live.lingting.framework.time.DateTime
import live.lingting.framework.util.DataSizeUtils.bytes
import live.lingting.framework.util.DigestUtils
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
internal class HuaweiObsTest {

    private val log = logger()

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

        val snowflake = Snowflake(0, 1)
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
        val key = "obs/b_t_l_m"
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
}
