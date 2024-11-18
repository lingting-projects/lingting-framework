package live.lingting.framework.stream

import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile
import java.nio.file.Files
import java.nio.file.Path
import live.lingting.framework.util.FileUtils

/**
 * @author lingting 2024-09-05 14:38
 */
class RandomAccessInputStream : InputStream {
    protected val file: RandomAccessFile

    val path: Path

    /**
     * 文件大小: bytes
     */
    val size: Long

    var isCloseAndDelete: Boolean = false

    constructor(`in`: InputStream) {
        val temp: File

        if (`in` is RandomAccessInputStream) {
            this.isCloseAndDelete = false
            temp = `in`.path.toFile()
        } else {
            this.isCloseAndDelete = true
            temp = FileUtils.createTemp(`in`, ".input", TEMP_DIR)
        }

        this.file = RandomAccessFile(temp, MODE)
        this.path = temp.toPath()
        this.size = temp.length()
    }

    constructor(path: String) : this(File(path))

    constructor(file: File) {
        this.file = RandomAccessFile(file, MODE)
        this.path = file.toPath()
        this.size = file.length()
    }

    constructor(path: Path) : this(path.toFile())


    fun seek(pos: Long) {
        file.seek(pos)
    }


    override fun close() {
        file.close()
        if (isCloseAndDelete) {
            Files.deleteIfExists(path)
        }
    }


    override fun read(): Int {
        return file.read()
    }


    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return file.read(b, off, len)
    }


    override fun reset() {
        file.seek(0)
    }

    companion object {
        const val MODE: String = "r"

        @JvmField
        val TEMP_DIR: File = RandomAccessOutputStream.TEMP_DIR
    }
}
