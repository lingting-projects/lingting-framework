package live.lingting.framework.multipart

import live.lingting.framework.data.DataSize
import live.lingting.framework.stream.CloneInputStream
import live.lingting.framework.stream.FileCloneInputStream
import live.lingting.framework.stream.RandomAccessInputStream
import live.lingting.framework.util.FileUtils
import live.lingting.framework.util.StreamUtils
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ConcurrentHashMap

/**
 * @author lingting 2024-09-05 14:47
 */
open class Multipart(
    /**
     * 唯一标识符
     */
    val id: String,
    /**
     * 分片原始内容
     */
    val source: File?,
    /**
     * 原始内容大小: byte
     */
    val size: DataSize,
    /**
     * 每个分片的最大大小: byte
     */
    val partSize: DataSize,
    /**
     * 所有分片
     */
    val parts: Collection<Part>,
    val partDir: File = TEMP_DIR,
) {

    companion object {

        @JvmField
        val TEMP_DIR: File = FileUtils.createTempDir("multipart")

        @JvmStatic
        fun builder(): MultipartBuilder {
            return MultipartBuilder()
        }

        /**
         * 计算对应大小和每个分片大小需要构造多少个分片
         * @param size     总大小
         * @param partSize 每个分片大小
         */
        @JvmStatic
        fun calculate(size: DataSize, partSize: DataSize): Long {
            val d = size / partSize
            val l = d.toLong()
            if (l.toDouble() == d) {
                return l
            }
            return l + 1
        }

        @JvmStatic
        fun split(size: DataSize, partSize: DataSize): Collection<Part> {
            val number = calculate(size, partSize)
            val parts: MutableList<Part> = ArrayList(number.toInt())
            for (i in 0 until number) {
                val start = partSize * i
                val middle = start + partSize - 1
                val end = if (middle >= size) size - 1 else middle
                val part = Part(i, start, end)
                parts.add(part)
            }
            return parts.toList()
        }
    }

    protected val cache: MutableMap<Long, File> = ConcurrentHashMap(parts.size)

    fun usePartSize(partSize: DataSize): Multipart {
        return usePartSize(partSize, id)
    }

    fun usePartSize(partSize: DataSize, id: String): Multipart {
        return Multipart(id, source, size, partSize, parts)
    }

    fun file(part: Part): File {
        return cache.computeIfAbsent(part.index) { k ->
            val file = FileUtils.createFile("$id.$k.part", partDir)
            RandomAccessInputStream(source!!).use { input ->
                input.seek(part.start.bytes)
                FileOutputStream(file).use { output ->
                    StreamUtils.write(input, output, part.size.bytes)
                }
            }
            file
        }
    }

    fun stream(part: Part): CloneInputStream {
        val file = file(part)
        return FileCloneInputStream(file)
    }

    fun clear() {
        cache.keys.forEach { clear(it) }
    }

    fun clear(part: Part) {
        clear(part.index)
    }

    protected fun clear(index: Long) {
        val file = cache.remove(index)
        FileUtils.delete(file)
    }

}
