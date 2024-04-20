package live.lingting.framework.id;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author lingting 2024-04-20 14:23
 */
@Getter
@RequiredArgsConstructor
public class SnowflakeParams {

	public static final SnowflakeParams DEFAULT = new SnowflakeParams();

	/**
	 * 2010年11月4日01:42:54 GMT
	 */
	public static final long DEFAULT_TIMESTAMP = 1288834974657L;

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
	 * 机器ID左移位数（12位）
	 */
	protected final long workerIdShift;

	/**
	 * 数据中心ID左移位数（12+5=17位）
	 */
	protected final long datacenterIdShift;

	/**
	 * 时间戳左移位数（12+5+5=22位）
	 */
	protected final long timestampLeftShift;

	/**
	 * 生成序列号的掩码（4095，这里12位）
	 */
	protected final long sequenceMask;

	public SnowflakeParams() {
		this(DEFAULT_TIMESTAMP, 5L, 5L, 12L);
	}

	public SnowflakeParams(long startTimestamp, long workerIdBits, long datacenterIdBits, long sequenceBits) {
		this(startTimestamp, workerIdBits, datacenterIdBits, sequenceBits, sequenceBits);
	}

	public SnowflakeParams(long startTimestamp, long workerIdBits, long datacenterIdBits, long sequenceBits,
			long workerIdShift) {
		this(startTimestamp, workerIdBits, datacenterIdBits, ~(-1L << workerIdBits), ~(-1L << datacenterIdBits),
				sequenceBits, workerIdShift, sequenceBits + workerIdBits,
				sequenceBits + workerIdBits + datacenterIdBits, ~(-1L << sequenceBits));
	}

}
