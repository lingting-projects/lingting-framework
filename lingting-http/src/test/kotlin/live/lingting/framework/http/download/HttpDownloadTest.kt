package live.lingting.framework.http.download

import live.lingting.framework.http.HttpClient
import live.lingting.framework.http.api.ApiClient
import live.lingting.framework.util.DataSizeUtils.bytes
import live.lingting.framework.util.DigestUtils
import live.lingting.framework.util.FileUtils
import live.lingting.framework.util.Slf4jUtils.logger
import live.lingting.framework.util.StreamUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrowsExactly
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileInputStream
import java.net.URI
import java.time.Duration

/**
 * @author lingting 2024-01-29 16:43
 */
internal class HttpDownloadTest {
    private val log = logger()

    val url: URI = URI.create(
        "https://maven.aliyun.com/repository/central/live/lingting/components/component-validation/0.0.1/component-validation-0.0.1.pom"
    )

    val md5: String = "2ce519cf7373a533e1fd297edb9ad1c3"

    var client: HttpClient = ApiClient.defaultClient

    @Test
    fun testJava() {
        client = HttpClient.java()
            .disableSsl()
            .callTimeout(Duration.ofSeconds(10))
            .connectTimeout(Duration.ofSeconds(15))
            .readTimeout(Duration.ofSeconds(30))
            .build()

        testSingle()
        testMulti()
    }

    @Test
    fun testOkhttp() {
        client = HttpClient.okhttp()
            .disableSsl()
            .callTimeout(Duration.ofSeconds(10))
            .connectTimeout(Duration.ofSeconds(15))
            .readTimeout(Duration.ofSeconds(30))
            .build()

        testSingle()
        testMulti()
    }

    fun testSingle() {
        val download = HttpDownload.single(url).client(client).build()

        assertFalse(download.isStart)
        assertFalse(download.isSuccess)
        assertFalse(download.isFinished)
        assertThrowsExactly(IllegalStateException::class.java) { download.await() }

        val await = download.start().await()

        if (!download.isSuccess) {
            log.error("error", download.ex)
        }

        assertEquals(download, await)
        assertTrue(download.isStart)
        assertTrue(download.isSuccess)
        assertTrue(download.isFinished)

        val file = download.file
        try {
            FileInputStream(file).use { stream ->
                val string = StreamUtils.toString(stream)
                val md5Hex = DigestUtils.md5Hex(string)
                assertEquals(md5, md5Hex)
            }
        } finally {
            FileUtils.delete(file)
        }
    }

    fun testMulti() {
        val download = HttpDownload.multi(url).partSize(50.bytes).client(client).build()

        assertFalse(download.isStart)
        assertFalse(download.isSuccess)
        assertFalse(download.isFinished)
        assertThrowsExactly(IllegalStateException::class.java) { download.await() }
        val await = download.start().await()

        if (!download.isSuccess) {
            log.error("error", download.ex)
        }

        assertEquals(download, await)
        assertTrue(download.isStart)
        assertTrue(download.isSuccess)
        assertTrue(download.isFinished)

        val file = download.file
        val temp = FileUtils.createTemp(".2")
        try {
            download.transferTo(temp)
            assertFile(file)
            assertFile(temp)
        } finally {
            FileUtils.delete(file)
            FileUtils.delete(temp)
        }
    }

    fun assertFile(target: File) {
        FileInputStream(target).use { stream ->
            val string = StreamUtils.toString(stream)
            val md5Hex = DigestUtils.md5Hex(string)
            assertEquals(md5, md5Hex)
        }
    }

}
