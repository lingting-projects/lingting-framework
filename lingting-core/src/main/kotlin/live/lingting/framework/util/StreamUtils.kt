package live.lingting.framework.util

import live.lingting.framework.function.ThrowingBiConsumerE
import live.lingting.framework.function.ThrowingBiFunctionE
import live.lingting.framework.function.ThrowingConsumerE
import live.lingting.framework.stream.CloneInputStream
import live.lingting.framework.stream.FileCloneInputStream
import live.lingting.framework.util.ByteUtils.isLine
import live.lingting.framework.util.ByteUtils.trimEndLine
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.function.BiConsumer

/**
 * @author lingting
 */
object StreamUtils {
    var readSize: Int = 1024 * 1024 * 10

    /**
     * 读取流, 如果 function 返回 false 则结束读取
     * @param function 消费读取到的数据, byte[] 数据, 读取长度. 返回false 则结束读取
     */
    @JvmStatic
    fun readByFlag(
        input: InputStream, size: Int,
        function: ThrowingBiFunctionE<ByteArray, Int, Boolean, IOException>
    ) {
        val bytes = ByteArray(size)
        var len: Int

        input.use {
            while (true) {
                len = input.read(bytes)
                // -1表示文件读完了
                if (len < 0) {
                    break
                }
                // 没有数据. 继续
                if (len == 0) {
                    continue
                }
                // 要求中止读取
                if (function.apply(bytes, len) == false) {
                    break
                }
            }
        }
    }

    @JvmStatic
    fun read(input: InputStream): ByteArray {
        val out = ByteArrayOutputStream()
        write(input, out)
        try {
            return out.toByteArray()
        } finally {
            close(out)
        }
    }

    @JvmStatic
    fun read(input: InputStream, consumer: ThrowingBiConsumerE<ByteArray, Int, IOException>) {
        read(input, readSize, consumer)
    }

    /**
     * 读取流
     * @param in       流
     * @param size     缓冲区大小
     * @param consumer 消费读取到的数据, byte[] 数据, 读取长度
     * @throws IOException 读取异常
     */
    @JvmStatic
    fun read(input: InputStream, size: Int, consumer: ThrowingBiConsumerE<ByteArray, Int, IOException>) {
        readByFlag(input, size) { bytes, length ->
            consumer.accept(bytes, length)
            true
        }
    }

    @JvmStatic
    fun readCopy(input: InputStream, consumer: ThrowingConsumerE<ByteArray, IOException>) {
        readCopy(input, readSize, consumer)
    }

    @JvmStatic
    fun readCopy(input: InputStream, size: Int, consumer: ThrowingConsumerE<ByteArray, IOException>) {
        read(input, size) { bytes, length ->
            val copy: ByteArray = bytes.copyOf(length)
            consumer.accept(copy)
        }
    }

    @JvmStatic
    fun write(input: InputStream, file: File) {
        Files.newOutputStream(file.toPath()).use { out ->
            write(input, out)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun write(input: InputStream, out: OutputStream, size: Int = readSize) {
        read(input, size) { bytes, len -> out.write(bytes, 0, len!!) }
    }

    @JvmStatic
    fun write(input: InputStream, out: OutputStream, length: Long) {
        write(input, out, readSize, length)
    }

    @JvmStatic
    fun write(input: InputStream, out: OutputStream, size: Int, length: Long) {
        val atomic = AtomicLong(0)
        readByFlag(input, size) { bytes, len ->
            // 计算剩余字节长度
            val remainLength = length - atomic.get()
            // 计算本次写入的字节长度, 不能大于剩余字节长度
            val writeLength = if (len!! > remainLength) remainLength.toInt() else len
            // 写入
            out.write(bytes, 0, writeLength)
            val existLength = atomic.addAndGet(len.toLong())
            existLength < length
        }
    }

    @JvmStatic
    fun toString(input: InputStream): String = toString(input, StandardCharsets.UTF_8)

    @JvmStatic
    fun toString(input: InputStream, charset: Charset): String {
        return toString(input, readSize, charset)
    }

    @JvmStatic
    fun toString(input: InputStream, size: Int, charset: Charset): String {
        ByteArrayOutputStream().use { out ->
            write(input, out, size)
            return out.toString(charset)
        }
    }

    /**
     * 从流中读取 int
     * @author lingting 2021-07-22 14:54
     */
    @JvmStatic
    fun readInt(input: InputStream, noOfBytes: Int, bigEndian: Boolean): Int {
        var ret = 0
        var sv = if (bigEndian) ((noOfBytes - 1) * 8) else 0
        val cnt = if (bigEndian) -8 else 8
        for (i in 0 until noOfBytes) {
            ret = ret or (input.read() shl sv)
            sv += cnt
        }
        return ret
    }

    @JvmStatic
    fun close(closeable: AutoCloseable?) {
        try {
            closeable?.close()
        } catch (e: Exception) {
            //
        }
    }

    @JvmStatic
    fun close(closeable: Closeable?) {
        try {
            closeable?.close()
        } catch (e: Exception) {
            //
        }
    }

    /**
     * 克隆文件流
     * 注意: 在使用后及时关闭复制流
     * @param stream 源流
     * @return 返回指定数量的从源流复制出来的只读流
     * @author lingting 2021-04-16 16:18
     */
    @JvmStatic
    fun clone(stream: InputStream): FileCloneInputStream {
        return clone(stream, readSize)
    }

    @JvmStatic
    fun clone(input: InputStream, size: Int): FileCloneInputStream {
        val file: File = FileUtils.createTemp(".clone", CloneInputStream.TEMP_DIR)
        FileOutputStream(file).use { output ->
            write(input, output, size)
        }
        return FileCloneInputStream(file)
    }

    /**
     * 读取流, 当读取完一行数据时, 消费该数据
     * @param in       流
     * @param charset  字符集
     * @param consumer 行数据消费, int: 行索引
     * @throws IOException 异常
     */
    @JvmStatic
    fun readLine(input: InputStream, charset: Charset, consumer: BiConsumer<Int, String>) {
        readLine(input, charset, readSize, consumer)
    }

    /**
     * 读取流, 当读取完一行数据时, 消费该数据
     * @param in       流
     * @param charset  字符集
     * @param consumer 行数据消费, int: 行索引
     * @throws IOException 异常
     */
    @JvmStatic
    fun readLine(input: InputStream, charset: Charset, size: Int, consumer: BiConsumer<Int, String>) {
        readLine(input, size) { index, bytes ->
            val string = String(bytes, charset)
            val clean: String = StringUtils.cleanBom(string)
            consumer.accept(index, clean)
        }
    }

    /**
     * 读取流, 当读取完一行数据时, 消费该数据
     * @param in       流
     * @param consumer 行数据消费, int: 行索引
     * @throws IOException 异常
     */
    @JvmStatic
    fun readLine(input: InputStream, consumer: BiConsumer<Int, ByteArray>) {
        readLine(input, readSize, consumer)
    }

    /**
     * 读取流, 当读取完一行数据时, 消费该数据
     * @param in       流
     * @param size     一次读取数据大小
     * @param consumer 行数据消费, int: 行索引
     * @throws IOException 异常
     */
    @JvmStatic
    fun readLine(input: InputStream, size: Int, consumer: BiConsumer<Int, ByteArray>) {
        val doConsumer = BiConsumer<Int, List<Byte>> { index, list ->
            val bytes: ByteArray = trimEndLine(list)
            consumer.accept(index, bytes)
        }

        val list: MutableList<Byte> = ArrayList()
        val atomic = AtomicInteger(0)

        read(input, size) { bytes, length ->
            for (i in 0 until length) {
                val b = bytes[i]
                list.add(b)
                // 如果是一整行数据, 则消费
                if (isLine(list)) {
                    // 获取行索引, 并自增
                    val index = atomic.getAndIncrement()
                    doConsumer.accept(index, list)
                    // 消费完毕, 结算
                    list.clear()
                }
            }
        }

        // 消费剩余的数据
        if (list.isNotEmpty()) {
            val index = atomic.get()
            doConsumer.accept(index, list)
        }
    }
}

