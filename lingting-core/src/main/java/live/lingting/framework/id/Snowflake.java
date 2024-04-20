package live.lingting.framework.id;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

/**
 * @author lingting 2024-04-18 20:23
 */
public class Snowflake {

	/**
	 * 雪花算法的开始时间戳（自定义）
	 */
	protected final long startTimestamp;

	/**
	 * 机器ID所占位数
	 */
	protected final long workerIdBits;

	/**
	 * 数据中心ID所占位数
	 */
	protected final long datacenterIdBits;

	/**
	 * 支持的最大机器ID数量
	 */
	protected final long maxWorkerId;

	/**
	 * 支持的最大数据中心ID数量
	 */
	protected final long maxDatacenterId;

	/**
	 * 序列号所占位数
	 */
	protected final long sequenceBits;

	/**
	 * 机器ID左移位数
	 */
	protected final long workerIdShift;

	/**
	 * 数据中心ID左移位数
	 */
	protected final long datacenterIdShift;

	/**
	 * 时间戳左移位数
	 */
	protected final long timestampLeftShift;

	/**
	 * 生成序列号的掩码
	 */
	protected final long sequenceMask;

	/**
	 * 机器ID
	 */
	protected final long workerId;

	/**
	 * 数据中心ID
	 */
	protected final long datacenterId;

	/**
	 * 毫秒内序列号
	 */
	protected long sequence = 0;

	/**
	 * 上次生成ID的时间戳
	 */
	protected long lastTimestamp = -1;

	/**
	 * 构造函数
	 * @param workerId 机器ID
	 * @param datacenterId 数据中心ID
	 */
	public Snowflake(long workerId, long datacenterId) {
		this(SnowflakeParams.DEFAULT, workerId, datacenterId);
	}

	/**
	 * 构造函数
	 * @param workerId 机器ID
	 * @param datacenterId 数据中心ID
	 */
	public Snowflake(SnowflakeParams params, long workerId, long datacenterId) {
		// 雪花算法的开始时间戳（自定义）
		this.startTimestamp = params.getStartTimestamp();
		// 机器ID所占位数
		this.workerIdBits = params.getWorkerIdBits();
		// 数据中心ID所占位数
		this.datacenterIdBits = params.getDatacenterIdBits();
		// 支持的最大机器ID数量
		this.maxWorkerId = params.getMaxWorkerId();
		// 支持的最大数据中心ID数量
		this.maxDatacenterId = params.getMaxDatacenterId();
		// 序列号所占位数
		this.sequenceBits = params.getSequenceBits();
		// 机器ID左移位数
		this.workerIdShift = params.getWorkerIdShift();
		// 数据中心ID左移位数
		this.datacenterIdShift = params.getDatacenterIdShift();
		// 时间戳左移位数
		this.timestampLeftShift = params.getTimestampLeftShift();
		// 生成序列号的掩码
		this.sequenceMask = params.getSequenceMask();

		if (workerId > maxWorkerId || workerId < 0) {
			throw new IllegalArgumentException("Worker ID cannot be greater than %d or less than 0");
		}
		if (datacenterId > maxDatacenterId || datacenterId < 0) {
			throw new IllegalArgumentException("Datacenter ID cannot be greater than %d or less than 0");
		}
		this.workerId = workerId;
		this.datacenterId = datacenterId;

	}

	/**
	 * 是否允许本次时钟回拨
	 * @param currentTimestamp 当前时间戳
	 * @return true表示允许时钟回拨, 会直接使用上一次的时间进行生成id
	 */
	protected boolean allowClockBackwards(long currentTimestamp) {
		return false;
	}

	protected void sleep() {
		LockSupport.parkNanos(10000);
	}

	/**
	 * 生成下一个ID
	 * @return 唯一ID
	 */
	public synchronized long nextId() {
		long timestamp = currentTimestamp();
		// 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回拨了
		if (timestamp < lastTimestamp) {
			if (!allowClockBackwards(timestamp)) {
				throw new IllegalStateException(
						"Clock moved backwards! current: %d; last: %d".formatted(timestamp, lastTimestamp));
			}
			// 允许回拨, 使用上次的时间
			timestamp = lastTimestamp;
		}
		return nextId(timestamp);
	}

	public String nextStr() {
		return String.valueOf(nextId());
	}

	public List<Long> nextIds(int count) {
		int max = Math.max(1, count);
		List<Long> ids = new ArrayList<>(max);
		for (int i = 0; i < max; i++) {
			ids.add(nextId());
		}
		return ids;
	}

	public List<String> nextStr(int count) {
		return nextIds(count).stream().map(String::valueOf).toList();
	}

	/**
	 * 依据指定时间戳生成id
	 */
	protected long nextId(long timestamp) {
		// 如果是同一时间生成的，则进行毫秒内序列
		if (lastTimestamp == timestamp) {
			sequence = (sequence + 1) & sequenceMask;
			// 毫秒内序列溢出
			if (sequence == 0) {
				// 休眠一下, 释放cpu
				sleep();
				// 阻塞到下一个毫秒, 获取新的时间戳
				timestamp = tilNextMillis(lastTimestamp);
			}
		}
		else {
			// 时间戳改变，毫秒内序列重置
			sequence = 0L;
		}
		// 上次生成ID的时间戳
		lastTimestamp = timestamp;

		// 按照规则拼装ID
		return ((timestamp - startTimestamp) << timestampLeftShift) | (datacenterId << datacenterIdShift)
				| (workerId << workerIdShift) | sequence;
	}

	/**
	 * 阻塞到下一个毫秒，直到获得新的时间戳
	 * @param lastTimestamp 上次生成ID的时间戳
	 * @return 当前时间戳
	 */
	protected long tilNextMillis(long lastTimestamp) {
		long timestamp = currentTimestamp();
		while (timestamp <= lastTimestamp) {
			timestamp = currentTimestamp();
		}
		return timestamp;
	}

	/**
	 * 返回当前时间戳
	 * @return 当前时间戳
	 */
	protected long currentTimestamp() {
		return System.currentTimeMillis();
	}

}
