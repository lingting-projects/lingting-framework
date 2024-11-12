package live.lingting.framework.http.download

import live.lingting.framework.http.download.HttpDownload.Companion.multi
import live.lingting.framework.http.download.HttpDownload.Companion.single
import live.lingting.framework.util.DigestUtils
import live.lingting.framework.util.FileUtils
import live.lingting.framework.util.StreamUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.URI
import java.security.NoSuchAlgorithmException

/**
 * @author lingting 2024-01-29 16:43
 */
internal class HttpDownloadTest {
    val url: URI = URI.create(
        "https://maven.aliyun.com/repository/central/live/lingting/components/component-validation/0.0.1/component-validation-0.0.1.pom"
    )

    val md5: String = "2ce519cf7373a533e1fd297edb9ad1c3"

    @Test
    @Throws(IOException::class, NoSuchAlgorithmException::class)
    fun single() {
        val download = single(url).build()

        Assertions.assertFalse(download.isStart)
        Assertions.assertFalse(download.isSuccess)
        Assertions.assertFalse(download.isFinished)
        Assertions.assertThrowsExactly(IllegalStateException::class.java) { download.await() }

        val await: HttpDownload = download.start().await()

        if (!download.isSuccess) {
            log.error("error", download.ex)
        }

        Assertions.assertEquals(download, await)
        Assertions.assertTrue(download.isStart)
        Assertions.assertTrue(download.isSuccess)
        Assertions.assertTrue(download.isFinished)

        val file = download.getFile()
        println(file.absolutePath)
        try {
            FileInputStream(file).use { stream ->
                val string = StreamUtils.toString(stream)
                val md5Hex = DigestUtils.md5Hex(string)
                Assertions.assertEquals(md5, md5Hex)
            }
        } finally {
            FileUtils.delete(file)
        }
    }

    @Test
    @Throws(IOException::class, NoSuchAlgorithmException::class)
    fun multi() {
        val download = multi(url).partSize(50)!!.build()

        Assertions.assertFalse(download.isStart)
        Assertions.assertFalse(download.isSuccess)
        Assertions.assertFalse(download.isFinished)
        Assertions.assertThrowsExactly(IllegalStateException::class.java) { download.await() }
        val await: HttpDownload = download.start().await()

        if (!download.isSuccess) {
            log.error("error", download.ex)
        }

        Assertions.assertEquals(download, await)
        Assertions.assertTrue(download.isStart)
        Assertions.assertTrue(download.isSuccess)
        Assertions.assertTrue(download.isFinished)

        val file = download.getFile()
        println(file.absolutePath)
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

    @Throws(NoSuchAlgorithmException::class, IOException::class)
    fun assertFile(target: File) {
        FileInputStream(target).use { stream ->
            val string = StreamUtils.toString(stream)
            val md5Hex = DigestUtils.md5Hex(string)
            Assertions.assertEquals(md5, md5Hex)
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(HttpDownloadTest::class.java)
    }
}
