package live.lingting.framework.multipart

import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer
import live.lingting.framework.stream.CloneInputStream
import live.lingting.framework.stream.FileCloneInputStream
import live.lingting.framework.stream.RandomAccessInputStream
import live.lingting.framework.util.FileUtils
import live.lingting.framework.util.StreamUtils

/**
 * @author lingting 2024-09-05 14:47
 */
class Multipart(
    /**
     * 唯一标识符
     */
    val id: String,
    /**
     *
     */
    val source: File?,
    /**
     * 原始内容大小: byte
     */
    val size: Long,
    /**
     * 每个分片的最大大小: byte
     */
    val partSize: Long,
    /**
     * 所有分片
     */
    val parts: Collection<Part>
) {

    protected val cache: MutableMap<Long, File> = ConcurrentHashMap(parts.size)

    fun usePartSize(partSize: Long): Multipart {
        return usePartSize(partSize, id)
    }

    fun usePartSize(partSize: Long, id: String): Multipart {
        return Multipart(id, source, size, partSize, parts)
    }

    fun file(part: Part): File {
        return cache.computeIfAbsent(part.index) { k ->
            val dir = File(TEMP_DIR, id)
            val temp: File = FileUtils.createTemp(".part$k", dir)
            RandomAccessInputStream(source!!).use { input ->
                input.seek(part.start)
                FileOutputStream(temp).use { output ->
                    StreamUtils.write(input, output, part.size)
                }
            }
            temp
        }
    }


    fun stream(part: Part): CloneInputStream {
        val file = file(part)
        return FileCloneInputStream(file)
    }

    fun clear() {
        cache.keys.forEach(Consumer { index -> this.clear(index) })
    }

    fun clear(part: Part) {
        clear(part.index)
    }

    protected fun clear(index: Long) {
        val file = cache.remove(index)
        FileUtils.delete(file)
    }

    companion object {
        @JvmField
        val TEMP_DIR: File = FileUtils.createTempDir("multipart")


        @JvmStatic
        fun builder(): MultipartBuilder {
            return MultipartBuilder()
        }

        /**
         * 计算对应大小和每个分片大小需要构造多少个分片
         *
         * @param size     总大小
         * @param partSize 每个分片大小
         */
        @JvmStatic
        fun calculate(size: Long, partSize: Long): Long {
            val l = size / partSize
            return if (size % partSize == 0L) l else l + 1
        }

        @JvmStatic
        fun split(size: Long, partSize: Long): Collection<Part> {
            val number = calculate(size, partSize)
            val parts: MutableList<Part> = ArrayList(number.toInt())
            for (i in 0 until number) {
                val start: Long = i * partSize
                val middle = start + partSize - 1
                val end = if (middle >= size) size - 1 else middle
                val part = Part(i, start, end)
                parts.add(part)
            }
            return parts.toList()
        }
    }
}
