package live.lingting.framework.id

import java.util.concurrent.locks.LockSupport
import kotlin.math.max
import live.lingting.framework.time.DateTime

/**
 * @author lingting 2024-04-18 20:23
 */
open class Snowflake(params: SnowflakeParams, workerId: Long, datacenterId: Long) {
    /**
     * 雪花算法的开始时间戳（自定义）
     */
    // 雪花算法的开始时间戳（自定义）
    protected val startTimestamp: Long = params.startTimestamp

    /**
     * 机器ID所占位数
     */
    // 机器ID所占位数
    protected val workerIdBits: Long = params.workerIdBits

    /**
     * 数据中心ID所占位数
     */
    // 数据中心ID所占位数
    protected val datacenterIdBits: Long = params.datacenterIdBits

    /**
     * 支持的最大机器ID数量
     */
    // 支持的最大机器ID数量
    protected val maxWorkerId: Long = params.maxWorkerId

    /**
     * 支持的最大数据中心ID数量
     */
    // 支持的最大数据中心ID数量
    protected val maxDatacenterId: Long = params.maxDatacenterId

    /**
     * 序列号所占位数
     */
    // 序列号所占位数
    protected val sequenceBits: Long = params.sequenceBits

    /**
     * 机器ID左移位数
     */
    // 机器ID左移位数
    protected val workerIdShift: Long = params.workerIdShift

    /**
     * 数据中心ID左移位数
     */
    // 数据中心ID左移位数
    protected val datacenterIdShift: Long = params.datacenterIdShift

    /**
     * 时间戳左移位数
     */
    // 时间戳左移位数
    protected val timestampLeftShift: Long = params.timestampLeftShift

    /**
     * 生成序列号的掩码
     */
    // 生成序列号的掩码
    protected val sequenceMask: Long = params.sequenceMask

    /**
     * 机器ID
     */
    protected val workerId: Long

    /**
     * 数据中心ID
     */
    protected val datacenterId: Long

    /**
     * 毫秒内序列号
     */
    protected var sequence: Long = 0

    /**
     * 上次生成ID的时间戳
     */
    protected var lastTimestamp: Long = -1

    /**
     * 构造函数
     * @param workerId 机器ID
     * @param datacenterId 数据中心ID
     */
    constructor(workerId: Long, datacenterId: Long) : this(SnowflakeParams.DEFAULT, workerId, datacenterId)

    /**
     * 构造函数
     * @param workerId 机器ID
     * @param datacenterId 数据中心ID
     */
    init {
        require(!(workerId > maxWorkerId || workerId < 0)) { "Worker ID cannot be greater than %d or less than 0" }
        require(!(datacenterId > maxDatacenterId || datacenterId < 0)) { "Datacenter ID cannot be greater than %d or less than 0" }
        this.workerId = workerId
        this.datacenterId = datacenterId
    }

    /**
     * 是否允许本次时钟回拨
     * @param currentTimestamp 当前时间戳
     * @return true表示允许时钟回拨, 会直接使用上一次的时间进行生成id
     */
    protected fun allowClockBackwards(currentTimestamp: Long): Boolean {
        return false
    }

    protected fun sleep() {
        LockSupport.parkNanos(10000)
    }

    /**
     * 生成下一个ID
     * @return 唯一ID
     */
    @Synchronized
    fun nextId(): Long {
        var timestamp = currentTimestamp()
        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回拨了
        if (timestamp < lastTimestamp) {
            check(allowClockBackwards(timestamp)) { "Clock moved backwards! current: $timestamp; last: $lastTimestamp" }
            // 允许回拨, 使用上次的时间
            timestamp = lastTimestamp
        }
        return nextId(timestamp)
    }

    fun nextStr(): String {
        return nextId().toString()
    }

    fun nextIds(count: Int): List<Long> {
        val max: Int = max(1, count)
        val ids: MutableList<Long> = ArrayList(max)
        (0 until max).forEach {
            ids.add(nextId())
        }
        return ids
    }

    fun nextStr(count: Int): List<String> {
        return nextIds(count).stream().map { obj -> java.lang.String.valueOf(obj) }.toList()
    }

    /**
     * 依据指定时间戳生成id
     */
    protected fun nextId(timestamp: Long): Long {
        // 如果是同一时间生成的，则进行毫秒内序列
        var timestamp = timestamp
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) and sequenceMask
            // 毫秒内序列溢出
            if (sequence == 0L) {
                // 休眠一下, 释放cpu
                sleep()
                // 阻塞到下一个毫秒, 获取新的时间戳
                timestamp = tilNextMillis(lastTimestamp)
            }
        } else {
            // 时间戳改变，毫秒内序列重置
            sequence = 0L
        }
        // 上次生成ID的时间戳
        lastTimestamp = timestamp

        // 按照规则拼装ID
        return (((timestamp - startTimestamp) shl timestampLeftShift.toInt()) or (datacenterId shl datacenterIdShift.toInt())
                or (workerId shl workerIdShift.toInt()) or sequence)
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param lastTimestamp 上次生成ID的时间戳
     * @return 当前时间戳
     */
    protected fun tilNextMillis(lastTimestamp: Long): Long {
        var timestamp = currentTimestamp()
        while (timestamp <= lastTimestamp) {
            timestamp = currentTimestamp()
        }
        return timestamp
    }

    /**
     * 返回当前时间戳
     * @return 当前时间戳
     */
    protected fun currentTimestamp(): Long {
        return DateTime.millis()
    }
}
