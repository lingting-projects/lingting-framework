package live.lingting.framework.function

import java.io.IOException

/**
 * @author lingting 2025/4/28 15:44
 */

fun interface StreamReadFlagSupplier {

    /**
     * @return true 表示继续读取流.
     */
    @Throws(IOException::class)
    fun get(bytes: ByteArray, length: Int): Boolean

}

fun interface StreamReadConsumer {

    /**
     * @return true 表示继续读取流.
     */
    @Throws(IOException::class)
    fun accept(bytes: ByteArray, length: Int)

}

fun interface StreamReadCopyConsumer {

    /**
     * @return true 表示继续读取流.
     */
    @Throws(IOException::class)
    fun accept(bytes: ByteArray)

}

fun interface StreamReadLineConsumer {

    /**
     * @return true 表示继续读取流.
     */
    @Throws(IOException::class)
    fun accept(text: String, index: Int)

}
