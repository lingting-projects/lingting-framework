package live.lingting.framework.data

import live.lingting.framework.data.DataSizeUnit.BIT
import live.lingting.framework.data.DataSizeUnit.BYTES
import live.lingting.framework.data.DataSizeUnit.GB
import live.lingting.framework.data.DataSizeUnit.KB
import live.lingting.framework.data.DataSizeUnit.MB
import live.lingting.framework.data.DataSizeUnit.PB
import live.lingting.framework.data.DataSizeUnit.TB

/**
 * @author lingting 2024/11/26 10:22
 */
data class DataSize(val bit: Long) {

    companion object {
        const val STEP: Long = 1024L

        @JvmStatic
        fun ofBit(value: Long): DataSize {
            return BIT.of(value)
        }

        @JvmStatic
        fun ofBytes(value: Long): DataSize {
            return BYTES.of(value)
        }

        @JvmStatic
        fun ofKb(value: Long): DataSize {
            return KB.of(value)
        }

        @JvmStatic
        fun ofMb(value: Long): DataSize {
            return MB.of(value)
        }

        @JvmStatic
        fun ofGb(value: Long): DataSize {
            return GB.of(value)
        }

        @JvmStatic
        fun ofTb(value: Long): DataSize {
            return TB.of(value)
        }

        @JvmStatic
        fun ofPb(value: Long): DataSize {
            return PB.of(value)
        }
    }

    val unit = DataSizeUnit.of(bit)

    val value by lazy { bit / unit.size }

    val bytes by lazy { bit / BYTES.size }

    val kb by lazy { bit / KB.size }

    val mb by lazy { bit / MB.size }

    val gb by lazy { bit / GB.size }

    val tb by lazy { bit / TB.size }

    val pb by lazy { bit / PB.size }

    override fun toString(): String {
        return "$value ${unit.text}"
    }

}

enum class DataSizeUnit(
    val size: Long,
    val text: String,
) {
    BIT(1, "Bit"),
    BYTES(BIT.size * DataSize.STEP, "Bytes"),
    KB(BYTES.size * DataSize.STEP, "KB"),
    MB(KB.size * DataSize.STEP, "MB"),
    GB(MB.size * DataSize.STEP, "GB"),
    TB(GB.size * DataSize.STEP, "TB"),
    PB(TB.size * DataSize.STEP, "PB"),

    ;

    companion object {

        @JvmStatic
        fun of(bit: Long): DataSizeUnit {
            return when {
                bit >= PB.size -> PB
                bit >= TB.size -> TB
                bit >= GB.size -> GB
                bit >= MB.size -> MB
                bit >= KB.size -> KB
                else -> BIT
            }
        }
    }

    fun of(value: Long): DataSize {
        return DataSize(value * size)
    }

}
