package live.lingting.framework.download;

import live.lingting.framework.exception.DownloadException;
import live.lingting.framework.multipart.Multipart;
import live.lingting.framework.multipart.Part;
import live.lingting.framework.multipart.PartTask;
import live.lingting.framework.thread.Async;
import live.lingting.framework.util.FileUtils;
import live.lingting.framework.util.ValueUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.concurrent.ThreadPoolExecutor;

import static live.lingting.framework.download.DownloadStatus.COMPLETED;
import static live.lingting.framework.download.DownloadStatus.RUNNING;
import static live.lingting.framework.download.DownloadStatus.WAIT;
import static live.lingting.framework.thread.Async.UNLIMITED;
import static live.lingting.framework.util.ValueUtils.simpleUuid;

/**
 * @author lingting 2024-09-06 16:39
 */
@SuppressWarnings({ "unchecked", "java:S1181", "java:S112" })
public abstract class MultipartDownload<D extends MultipartDownload<D>> implements Download {

	public static final File TEMP_DIR = FileUtils.createTempDir("download");

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Getter
	protected final String url;

	/**
	 * 文件大小. 不知道就写null. 为null或小于1时会调用 {@link MultipartDownload#size()} 方法获取
	 */
	@Getter
	protected final Long size;

	/**
	 * 是否存在多个分片.
	 */
	@Getter
	protected final boolean multi;

	@Getter
	protected final long threadLimit;

	@Getter
	protected final long partSize;

	@Getter
	protected final long maxRetryCount;

	@Getter
	protected final Duration timeout;

	protected final ThreadPoolExecutor executor;

	protected final String id;

	@Getter
	protected DownloadStatus downloadStatus = WAIT;

	protected DownloadException ex = null;

	protected File file;

	protected MultipartDownload(DownloadBuilder<?> builder) throws IOException {
		this.url = builder.url;
		this.size = builder.size;
		this.multi = builder.multi;
		this.threadLimit = builder.threadLimit;
		this.partSize = builder.partSize;
		this.maxRetryCount = builder.maxRetryCount;
		this.timeout = builder.timeout;
		this.executor = builder.executor;
		this.id = simpleUuid();
		this.file = FileUtils.createTemp(id, TEMP_DIR);
	}

	@Override
	public synchronized D start() {
		if (!isStart() && !isFinished()) {
			downloadStatus = RUNNING;
			doStart();
		}
		return (D) this;
	}

	protected void doStart() {
		Async async = threadLimit == UNLIMITED ? new Async() : new Async(threadLimit + 1);
		async.submit("download-" + id, () -> {
			try {
				long fileSize = size == null || size < 1 ? size() : size;
				// 不使用多分配下载时, 只设置一个分片
				Multipart multipart = new Multipart(id, fileSize, multi ? partSize : fileSize);
				DownloadFileMultipartTask task = new DownloadFileMultipartTask(multipart, maxRetryCount, async, file,
						this::download);
				task.start().await(timeout);
				if (task.hasFailed()) {
					PartTask t = task.tasksFailed().get(0);
					throw t.getT();
				}
			}
			catch (Throwable e) {
				ex = new DownloadException("download start error!", e);
			}
			finally {
				downloadStatus = COMPLETED;
			}
		});
	}

	@Override
	public D await() {
		if (!isStart()) {
			throw new IllegalStateException("download not start!");
		}

		ValueUtils.awaitTrue(this::isFinished);
		return (D) this;
	}

	@Override
	public boolean isStart() {
		return downloadStatus != WAIT;
	}

	@Override
	public boolean isFinished() {
		return downloadStatus == COMPLETED;
	}

	@Override
	public boolean isSuccess() {
		return isFinished() && ex == null;
	}

	@Override
	public File getFile() throws DownloadException {
		await();
		if (ex != null) {
			throw ex;
		}
		return file;
	}

	public abstract long size() throws IOException;

	public abstract InputStream download(Part part) throws Throwable;

}
