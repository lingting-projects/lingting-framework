package live.lingting.framework.data

import java.io.Serializable
import java.math.BigDecimal
import java.math.RoundingMode
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
    val bytes: Long,
    val scale: Int = 2,
) : Comparable<DataSize>, Serializable {

    companion object {

        const val STEP: Long = 1024L

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

    val unit = DataSizeUnit.of(bytes)

    val value by lazy { bytes / unit.size }

    val bit by lazy { bytes * STEP }

    val kb by lazy { bytes / KB.size }

    val mb by lazy { bytes / MB.size }

    val gb by lazy { bytes / GB.size }

    val tb by lazy { bytes / TB.size }

    val pb by lazy { bytes / PB.size }

    val scaleValue: BigDecimal by lazy {
        (bytes / unit.size.toDouble())
            .toBigDecimal()
            .setScale(scale, RoundingMode.CEILING)
            .stripTrailingZeros()
    }

    override fun compareTo(other: DataSize): Int {
        return bytes.compareTo(other.bytes)
    }

    override fun hashCode(): Int {
        return bytes.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || other !is DataSize) {
            return false
        }
        return bytes == other.bytes
    }

    override fun toString(): String {
        return "${scaleValue.toPlainString()} ${unit.text}"
    }

    // region operator

    operator fun plus(other: DataSize): DataSize {
        return DataSize(bytes + other.bytes, scale)
    }

    operator fun plus(other: Number): DataSize {
        return DataSize(bytes + other.toLong(), scale)
    }

    operator fun minus(other: DataSize): DataSize {
        return DataSize(bytes - other.bytes, scale)
    }

    operator fun minus(other: Number): DataSize {
        return DataSize(bytes - other.toLong(), scale)
    }

    operator fun times(other: DataSize): DataSize {
        return DataSize(bytes * other.bytes, scale)
    }

    operator fun times(other: Number): DataSize {
        return DataSize(bytes * other.toLong(), scale)
    }

    operator fun div(other: DataSize): DataSize {
        return DataSize(bytes / other.bytes, scale)
    }

    operator fun div(other: Number): DataSize {
        return DataSize(bytes / other.toLong(), scale)
    }

    operator fun rem(other: Byte): Long = bytes % other

    operator fun rem(other: Double): Double = bytes % other

    operator fun rem(other: Float): Float = bytes % other

    operator fun rem(other: Int): Long = bytes % other

    operator fun rem(other: Long): Long = bytes % other

    operator fun rem(other: Short): Long = bytes % other

    // endregion

}

