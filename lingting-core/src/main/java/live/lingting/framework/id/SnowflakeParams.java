package live.lingting.framework.id;

/**
 * @author lingting 2024-04-20 14:23
 */
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

	public SnowflakeParams(long startTimestamp, long workerIdBits, long datacenterIdBits, long maxWorkerId, long maxDatacenterId, long sequenceBits, long workerIdShift, long datacenterIdShift, long timestampLeftShift, long sequenceMask) {
		this.startTimestamp = startTimestamp;
		this.workerIdBits = workerIdBits;
		this.datacenterIdBits = datacenterIdBits;
		this.maxWorkerId = maxWorkerId;
		this.maxDatacenterId = maxDatacenterId;
		this.sequenceBits = sequenceBits;
		this.workerIdShift = workerIdShift;
		this.datacenterIdShift = datacenterIdShift;
		this.timestampLeftShift = timestampLeftShift;
		this.sequenceMask = sequenceMask;
	}

	public long getStartTimestamp() {return this.startTimestamp;}

	public long getWorkerIdBits() {return this.workerIdBits;}

	public long getDatacenterIdBits() {return this.datacenterIdBits;}

	public long getMaxWorkerId() {return this.maxWorkerId;}

	public long getMaxDatacenterId() {return this.maxDatacenterId;}

	public long getSequenceBits() {return this.sequenceBits;}

	public long getWorkerIdShift() {return this.workerIdShift;}

	public long getDatacenterIdShift() {return this.datacenterIdShift;}

	public long getTimestampLeftShift() {return this.timestampLeftShift;}

	public long getSequenceMask() {return this.sequenceMask;}
}
