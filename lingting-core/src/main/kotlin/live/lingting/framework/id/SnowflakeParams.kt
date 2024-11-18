package live.lingting.framework.id

/**
 * @author lingting 2024-04-20 14:23
 */
class SnowflakeParams(
    /**
     * 雪花算法的开始时间戳（自定义）
     */
    val startTimestamp: Long,
    /**
     * 机器ID所占位数
     */
    val workerIdBits: Long,
    /**
     * 数据中心ID所占位数
     */
    val datacenterIdBits: Long,
    /**
     * 支持的最大机器ID数量
     */
    val maxWorkerId: Long,
    /**
     * 支持的最大数据中心ID数量
     */
    val maxDatacenterId: Long,
    /**
     * 序列号所占位数
     */
    val sequenceBits: Long,
    /**
     * 机器ID左移位数（12位）
     */
    val workerIdShift: Long,
    /**
     * 数据中心ID左移位数（12+5=17位）
     */
    val datacenterIdShift: Long,
    /**
     * 时间戳左移位数（12+5+5=22位）
     */
    val timestampLeftShift: Long,
    /**
     * 生成序列号的掩码（4095，这里12位）
     */
    val sequenceMask: Long
) {

    constructor(
        startTimestamp: Long = DEFAULT_TIMESTAMP, workerIdBits: Long = 5L, datacenterIdBits: Long = 5L, sequenceBits: Long = 12L,
        workerIdShift: Long = sequenceBits
    ) : this(
        startTimestamp, workerIdBits, datacenterIdBits, (-1L shl workerIdBits.toInt()).inv(), (-1L shl datacenterIdBits.toInt()).inv(),
        sequenceBits, workerIdShift, sequenceBits + workerIdBits,
        sequenceBits + workerIdBits + datacenterIdBits, (-1L shl sequenceBits.toInt()).inv()
    )

    companion object {
        @JvmField
        val DEFAULT: SnowflakeParams = SnowflakeParams()

        /**
         * 2010年11月4日01:42:54 GMT
         */
        const val DEFAULT_TIMESTAMP: Long = 1288834974657L
    }
}
