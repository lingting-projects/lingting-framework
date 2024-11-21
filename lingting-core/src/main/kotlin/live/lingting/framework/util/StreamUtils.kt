package live.lingting.framework.util

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
import live.lingting.framework.function.ThrowingBiConsumerE
import live.lingting.framework.function.ThrowingBiFunctionE
import live.lingting.framework.function.ThrowingConsumerE
import live.lingting.framework.stream.CloneInputStream
import live.lingting.framework.stream.FileCloneInputStream

/**
 * @author lingting
 */
object StreamUtils {
    var readSize: Int = 1024 * 1024 * 10

    /**
     * 读取流, 如果 function 返回 false 则结束读取
     *
     * @param function 消费读取到的数据, byte[] 数据, 读取长度. 返回false 则结束读取
     */
    @JvmStatic
    fun readByFlag(
        `in`: InputStream, size: Int,
        function: ThrowingBiFunctionE<ByteArray, Int, Boolean, IOException>
    ) {
        val bytes = ByteArray(size)
        var len: Int

        `in`.use {
            while (true) {
                len = `in`.read(bytes)
                // 已读取长度小于1 或者 消费数据, 返回标志位为false
                val isBreak = len < 1 || java.lang.Boolean.FALSE == function.apply(bytes, len)
                if (isBreak) {
                    break
                }
            }
        }
    }


    @JvmStatic
    fun read(`in`: InputStream): ByteArray {
        val out = ByteArrayOutputStream()
        write(`in`, out)
        try {
            return out.toByteArray()
        } finally {
            close(out)
        }
    }

    @JvmStatic
    fun read(`in`: InputStream, consumer: ThrowingBiConsumerE<ByteArray, Int, IOException>) {
        read(`in`, readSize, consumer)
    }

    /**
     * 读取流
     *
     * @param in       流
     * @param size     缓冲区大小
     * @param consumer 消费读取到的数据, byte[] 数据, 读取长度
     * @throws IOException 读取异常
     */
    @JvmStatic
    fun read(`in`: InputStream, size: Int, consumer: ThrowingBiConsumerE<ByteArray, Int, IOException>) {
        readByFlag(`in`, size) { bytes, length ->
            consumer.accept(bytes, length)
            true
        }
    }

    @JvmStatic
    fun readCopy(`in`: InputStream, consumer: ThrowingConsumerE<ByteArray, IOException>) {
        readCopy(`in`, readSize, consumer)
    }

    @JvmStatic
    fun readCopy(`in`: InputStream, size: Int, consumer: ThrowingConsumerE<ByteArray, IOException>) {
        read(`in`, size) { bytes, length ->
            val copy: ByteArray = bytes.copyOf(length)
            consumer.accept(copy)
        }
    }

    @JvmStatic
    fun write(`in`: InputStream, file: File) {
        Files.newOutputStream(file.toPath()).use { out ->
            write(`in`, out)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun write(`in`: InputStream, out: OutputStream, size: Int = readSize) {
        read(`in`, size) { bytes, len -> out.write(bytes, 0, len!!) }
    }

    @JvmStatic
    fun write(`in`: InputStream, out: OutputStream, length: Long) {
        write(`in`, out, readSize, length)
    }

    @JvmStatic
    fun write(`in`: InputStream, out: OutputStream, size: Int, length: Long) {
        val atomic = AtomicLong(0)
        readByFlag(`in`, size) { bytes, len ->
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
    fun toString(`in`: InputStream): String = toString(`in`, StandardCharsets.UTF_8)

    @JvmStatic
    fun toString(`in`: InputStream, charset: Charset): String {
        return toString(`in`, readSize, charset)
    }

    @JvmStatic
    fun toString(`in`: InputStream, size: Int, charset: Charset): String {
        ByteArrayOutputStream().use { out ->
            write(`in`, out, size)
            return out.toString(charset)
        }
    }

    /**
     * 从流中读取 int
     *
     * @author lingting 2021-07-22 14:54
     */
    @JvmStatic
    fun readInt(`is`: InputStream, noOfBytes: Int, bigEndian: Boolean): Int {
        var ret = 0
        var sv = if (bigEndian) ((noOfBytes - 1) * 8) else 0
        val cnt = if (bigEndian) -8 else 8
        for (i in 0 until noOfBytes) {
            ret = ret or (`is`.read() shl sv)
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
     *
     *
     * 注意: 在使用后及时关闭复制流
     *
     *
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
     *
     * @param in       流
     * @param charset  字符集
     * @param consumer 行数据消费, int: 行索引
     * @throws IOException 异常
     */
    @JvmStatic
    fun readLine(`in`: InputStream, charset: Charset, consumer: BiConsumer<Int, String>) {
        readLine(`in`, charset, readSize, consumer)
    }

    /**
     * 读取流, 当读取完一行数据时, 消费该数据
     *
     * @param in       流
     * @param charset  字符集
     * @param consumer 行数据消费, int: 行索引
     * @throws IOException 异常
     */
    @JvmStatic
    fun readLine(`in`: InputStream, charset: Charset, size: Int, consumer: BiConsumer<Int, String>) {
        readLine(`in`, size) { index, bytes ->
            val string = String(bytes, charset)
            val clean: String = StringUtils.cleanBom(string)
            consumer.accept(index, clean)
        }
    }

    /**
     * 读取流, 当读取完一行数据时, 消费该数据
     *
     * @param in       流
     * @param consumer 行数据消费, int: 行索引
     * @throws IOException 异常
     */
    @JvmStatic
    fun readLine(`in`: InputStream, consumer: BiConsumer<Int, ByteArray>) {
        readLine(`in`, readSize, consumer)
    }

    /**
     * 读取流, 当读取完一行数据时, 消费该数据
     *
     * @param in       流
     * @param size     一次读取数据大小
     * @param consumer 行数据消费, int: 行索引
     * @throws IOException 异常
     */
    @JvmStatic
    fun readLine(`in`: InputStream, size: Int, consumer: BiConsumer<Int, ByteArray>) {
        val doConsumer = BiConsumer<Int, List<Byte>> { index, list ->
            val bytes: ByteArray = ByteUtils.trimEndLine(list)
            consumer.accept(index, bytes)
        }

        val list: MutableList<Byte> = ArrayList()
        val atomic = AtomicInteger(0)

        read(`in`, size) { bytes, length ->
            for (i in 0 until length) {
                val b = bytes[i]
                list.add(b)
                // 如果是一整行数据, 则消费
                if (ByteUtils.isLine(list)) {
                    // 获取行索引, 并自增
                    val index = atomic.getAndIncrement()
                    doConsumer.accept(index, list)
                    // 消费完毕, 结算
                    list.clear()
                }
            }
        }

        // 消费剩余的数据
        if (!list.isEmpty()) {
            val index = atomic.get()
            doConsumer.accept(index, list)
        }
    }
}

