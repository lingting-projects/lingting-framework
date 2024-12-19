package live.lingting.framework.data

import java.math.BigDecimal
import java.math.RoundingMode
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
data class DataSize @JvmOverloads constructor(
    val bit: Long,
    val scale: Int = 2,
) {

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

    val scaleValue: BigDecimal by lazy { (bit / unit.size.toDouble()).toBigDecimal().setScale(scale, RoundingMode.CEILING).stripTrailingZeros() }

    override fun hashCode(): Int {
        return bit.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || other !is DataSize) {
            return false
        }
        return bit == other.bit
    }

    override fun toString(): String {
        return "${scaleValue.toPlainString()} ${unit.text}"
    }

    // region operator

    operator fun plus(other: DataSize): DataSize {
        return DataSize(bit + other.bit, scale)
    }

    operator fun plus(other: Number): DataSize {
        return DataSize(bit + other.toLong(), scale)
    }

    operator fun minus(other: DataSize): DataSize {
        return DataSize(bit - other.bit, scale)
    }

    operator fun minus(other: Number): DataSize {
        return DataSize(bit - other.toLong(), scale)
    }

    operator fun times(other: DataSize): DataSize {
        return DataSize(bit * other.bit, scale)
    }

    operator fun times(other: Number): DataSize {
        return DataSize(bit * other.toLong(), scale)
    }

    operator fun div(other: DataSize): DataSize {
        return DataSize(bit / other.bit, scale)
    }

    operator fun div(other: Number): DataSize {
        return DataSize(bit / other.toLong(), scale)
    }

    operator fun rem(other: Byte): Long = bit % other

    operator fun rem(other: Double): Double = bit % other

    operator fun rem(other: Float): Float = bit % other

    operator fun rem(other: Int): Long = bit % other

    operator fun rem(other: Long): Long = bit % other

    operator fun rem(other: Short): Long = bit % other

    // endregion

}

