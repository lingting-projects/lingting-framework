package live.lingting.framework.download;

import live.lingting.framework.util.FileUtils;
import live.lingting.framework.util.SystemUtils;
import live.lingting.framework.util.ThreadUtils;
import lombok.Getter;

import java.io.File;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author lingting 2024-01-16 19:33
 */
@Getter
@SuppressWarnings("unchecked")
public abstract class AbstractDownloadBuilder<B extends AbstractDownloadBuilder<B>> {

	protected static final int DEFAULT_MAX_THREAD_COUNT = 10;

	/**
	 * 默认10M
	 */
	protected static final long DEFAULT_MAX_SHARD_SIZE = 10485760;

	/**
	 * 文件下载地址
	 */
	protected final String url;

	/**
	 * 文件存放文件夹
	 */
	protected File dir = new File(SystemUtils.tmpDirLingting(), "download");

	/**
	 * 为空从url解析
	 */
	protected String filename;

	protected ThreadPoolExecutor executor = ThreadUtils.instance().getPool();

	protected boolean multi = false;

	/**
	 * 文件大小, 用于多线程下载时进行分片. 单位: bytes
	 * <p>
	 * 设置为null或者小于1时自动解析文件具体大小
	 * </p>
	 */
	protected Long fileSize;

	/**
	 * 最大启动线程数
	 */
	protected int maxThreadCount = DEFAULT_MAX_THREAD_COUNT;

	/**
	 * 每个分片的最大大小, 单位: bytes
	 */
	protected long maxShardSize = DEFAULT_MAX_SHARD_SIZE;

	protected AbstractDownloadBuilder(String url) {
		String[] split = url.split("/");

		this.url = url;
		this.filename = split[split.length - 1];
	}

	public B dir(File dir) {
		FileUtils.createDir(dir);
		this.dir = dir;
		return (B) this;
	}

	public B filename(String filename) {
		this.filename = filename;
		return (B) this;
	}

	public B executor(ThreadPoolExecutor executor) {
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

	public B fileSize(Long fileSize) {
		this.fileSize = fileSize;
		return (B) this;
	}

	public B maxThreadCount(int maxThreadCount) {
		this.maxThreadCount = safeDefault(maxThreadCount, DEFAULT_MAX_THREAD_COUNT);
		return (B) this;
	}

	public B maxShardSize(long maxShardSize) {
		this.maxShardSize = safeDefault(maxShardSize, DEFAULT_MAX_SHARD_SIZE);
		return (B) this;
	}

	public abstract Download build();

	/**
	 * 将原值进行安装判断, 如果不满足则设置为默认值
	 * @param t 原值
	 * @param d 默认值
	 * @return 结果
	 */
	protected <T extends Number> T safeDefault(T t, T d) {
		if (t == null || t.longValue() < 1) {
			return d;
		}
		return t;
	}

}
