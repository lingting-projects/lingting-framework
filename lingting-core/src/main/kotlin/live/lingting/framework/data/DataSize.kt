package live.lingting.framework.data

import live.lingting.framework.data.DataSizeUnit.BYTES
import live.lingting.framework.data.DataSizeUnit.GB
import live.lingting.framework.data.DataSizeUnit.KB
import live.lingting.framework.data.DataSizeUnit.MB
import live.lingting.framework.data.DataSizeUnit.PB
import live.lingting.framework.data.DataSizeUnit.TB
import java.io.Serializable
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * @author lingting 2024/11/26 10:22
 */
data class DataSize @JvmOverloads constructor(
    val bytes: Long,
    val scale: Int = 2,
) : Comparable<DataSize>, Serializable {

    companion object {

        @JvmField
        val ZERO = ofBytes(0)

        const val STEP: Long = 1024L

        private val pattern = Regex("""(\d+(?:\.\d+)?)\s*([A-Za-z]+)""")

        @JvmStatic
        fun of(any: Any?): DataSize? {
            val source = any?.toString()
            if (source.isNullOrBlank()) {
                return null
            }

            val find = pattern.find(source.trim())

            if (find?.destructured == null) {
                val l = source.toLongOrNull()
                if (l == null) {
                    return null
                }
                return ofBytes(l)
            }

            val (valueStr, unitStr) = find.destructured

            val value = valueStr.toBigDecimalOrNull() ?: return null
            val unit = DataSizeUnit.entries.firstOrNull {
                it.text.equals(unitStr, ignoreCase = true)
            } ?: return null
            return unit.of(value)
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

    operator fun plus(other: DataSize) = DataSize(bytes + other.bytes, scale)

    operator fun plus(other: Number) = DataSize(bytes + other.toLong(), scale)

    operator fun minus(other: DataSize) = DataSize(bytes - other.bytes, scale)

    operator fun minus(other: Number) = DataSize(bytes - other.toLong(), scale)

    operator fun times(other: DataSize) = DataSize(bytes * other.bytes, scale)

    operator fun times(other: Number) = DataSize(bytes * other.toLong(), scale)

    operator fun div(other: DataSize) = this / other.bytes

    operator fun div(other: Number) = bytes / other.toDouble()

    operator fun rem(other: DataSize) = bytes % other.bytes

    operator fun rem(other: Byte) = bytes % other

    operator fun rem(other: Double) = bytes % other

    operator fun rem(other: Float) = bytes % other

    operator fun rem(other: Int) = bytes % other

    operator fun rem(other: Long) = bytes % other

    operator fun rem(other: Short) = bytes % other

    // endregion

}

