package live.lingting.framework.stream

import live.lingting.framework.util.FileUtils
import java.io.File
import java.io.OutputStream
import java.io.RandomAccessFile
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author lingting 2024-01-16 20:29
 */
open class RandomAccessOutputStream(file: File = FileUtils.createTemp(".output", TEMP_DIR)) : OutputStream() {

    companion object {
        const val MODE: String = "rw"

        @JvmField
        val TEMP_DIR: File = FileUtils.createTempDir("random")
    }

    protected val file: RandomAccessFile = RandomAccessFile(file, MODE)

    val path: Path = file.toPath()

    constructor(path: String) : this(File(path))

    constructor(path: Path) : this(path.toFile())

    fun seek(pos: Long) {
        file.seek(pos)
    }

    fun size(): Long {
        return Files.size(path)
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        file.write(b, off, len)
    }

    override fun close() {
        file.close()
    }

    override fun write(b: Int) {
        file.write(b)
    }

}
