package live.lingting.framework.http.download

import java.io.File
import java.io.FileInputStream
import java.net.URI
import live.lingting.framework.util.DigestUtils
import live.lingting.framework.util.FileUtils
import live.lingting.framework.util.Slf4jUtils.logger
import live.lingting.framework.util.StreamUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrowsExactly
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-01-29 16:43
 */
internal class HttpDownloadTest {
    val url: URI = URI.create(
        "https://maven.aliyun.com/repository/central/live/lingting/components/component-validation/0.0.1/component-validation-0.0.1.pom"
    )

    val md5: String = "2ce519cf7373a533e1fd297edb9ad1c3"

    @Test
    fun testSingle() {
        val download = HttpDownload.single(url).build()

        assertFalse(download.isStart)
        assertFalse(download.isSuccess)
        assertFalse(download.isFinished)
        assertThrowsExactly(IllegalStateException::class.java) { download.await() }

        val await: HttpDownload = download.start().await()

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

    @Test
    fun testMulti() {
        val download = HttpDownload.multi(url).partSize(50).build()

        assertFalse(download.isStart)
        assertFalse(download.isSuccess)
        assertFalse(download.isFinished)
        assertThrowsExactly(IllegalStateException::class.java) { download.await() }
        val await: HttpDownload = download.start().await()

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

    companion object {
        private val log = logger()
    }
}
