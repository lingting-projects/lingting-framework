package live.lingting.framework.download;

import live.lingting.framework.util.ThreadUtils;
import lombok.Getter;

import java.time.Duration;
import java.util.concurrent.ExecutorService;

/**
 * @author lingting 2024-01-16 19:33
 */
@Getter
@SuppressWarnings("unchecked")
public abstract class DownloadBuilder<B extends DownloadBuilder<B>> {

	protected static final int DEFAULT_THREAD_LIMIT = 20;

	/**
	 * 默认10M
	 */
	protected static final long DEFAULT_PART_SIZE = 10485760;

	protected static final long DEFAULT_MAX_RETRY_COUNT = 3;

	protected static final Duration DEFAULT_TIMEOUT = null;

	/**
	 * 文件下载地址
	 */
	protected final String url;

	protected boolean multi = false;

	protected ExecutorService executor = ThreadUtils.executor();

	/**
	 * 文件大小, 用于多线程下载时进行分片. 单位: bytes
	 * <p>
	 * 设置为null或者小于1时调用size方法解析
	 * </p>
	 */
	protected Long size;

	/**
	 * 最大启动线程数
	 */
	protected int threadLimit = DEFAULT_THREAD_LIMIT;

	/**
	 * 每个分片的最大大小, 单位: bytes
	 */
	protected long partSize = DEFAULT_PART_SIZE;

	protected long maxRetryCount = DEFAULT_MAX_RETRY_COUNT;

	protected Duration timeout = null;

	protected DownloadBuilder(String url) {
		this.url = url;
	}

	public B executor(ExecutorService executor) {
		this.executor = executor;
		return (B) this;
	}

	public B single() {
		this.multi = false;
		return (B) this;
	}

	public B multi() {
		this.multi = true;
		return (B) this;
	}

	public B size(Long size) {
		this.size = size;
		return (B) this;
	}

	public B threadLimit(int maxThreadCount) {
		this.threadLimit = safeDefault(maxThreadCount, DEFAULT_THREAD_LIMIT);
		return (B) this;
	}

	public B partSize(long partSize) {
		this.partSize = safeDefault(partSize, DEFAULT_PART_SIZE);
		return (B) this;
	}

	public B maxRetryCount(long maxRetryCount) {
		this.maxRetryCount = safeDefault(maxRetryCount, DEFAULT_MAX_RETRY_COUNT);
		return (B) this;
	}

	public B timeout(Duration timeout) {
		this.timeout = safeDefault(timeout, DEFAULT_TIMEOUT);
		return (B) this;
	}

	public abstract Download build();

	/**
	 * 将原值进行安全判断, 如果不满足则设置为默认值
	 * @param t 原值
	 * @param d 默认值
	 * @return 结果
	 */
	protected <T> T safeDefault(T t, T d) {
		if (t == null) {
			return d;
		}
		if (t instanceof Number number && number.longValue() < 1) {
			return d;
		}
		if (t instanceof Duration duration && duration.isNegative()) {
			return d;
		}
		return t;
	}

}
