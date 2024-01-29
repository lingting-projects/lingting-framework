package live.lingting.framework.download;

import live.lingting.framework.exception.DownloadException;
import live.lingting.framework.function.ThrowingRunnable;
import live.lingting.framework.util.FileUtils;
import live.lingting.framework.util.ValueUtils;
import lombok.Getter;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

/**
 * @author lingting 2024-01-16 19:33
 */
@SuppressWarnings("unchecked")
public abstract class AbstractDownload<D extends AbstractDownload<D>> implements Download {

	protected final Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

	protected final ThreadPoolExecutor executor;

	@Getter
	protected final String url;

	@Getter
	protected final File dir;

	@Getter
	protected final String filename;

	@Getter
	protected boolean start = false;

	@Getter
	protected boolean finished = false;

	protected DownloadException ex = null;

	protected <B extends AbstractDownloadBuilder<B>> AbstractDownload(B builder) {
		this.executor = builder.executor;
		this.url = builder.url;
		this.dir = builder.dir;
		this.filename = builder.filename;
	}

	/**
	 * 开始下载, 如果已开始或者已完成则不执行下载任务
	 */
	public synchronized D start() throws IOException {
		if (isStart()) {
			return (D) this;
		}
		start = true;

		if (!isFinished()) {
			String name = "DOWNLOAD-" + filename;
			async(name, this::doStart, e -> {
				log.error("download error!", e);
				upsertEx(e);
			});
		}
		return (D) this;
	}

	/**
	 * 执行下载任务
	 */
	protected abstract void doStart() throws IOException;

	/**
	 * 等待下载完成
	 */
	public D await() throws InterruptedException {
		if (!isStart()) {
			throw new DownloadException("download not start");
		}
		ValueUtils.awaitTrue(this::isFinished);
		return (D) this;
	}

	/**
	 * 判断是否下载成功, 注意: 在未下载完成之前, 该方法均返回true
	 */
	public boolean isSuccess() {
		return ex == null;
	}

	/**
	 * 等待任务下载完成, 并且返回下载后的文件
	 */
	public File getFile() throws InterruptedException, DownloadException {
		await();
		if (ex != null) {
			throw ex;
		}
		return target();
	}

	protected File target() {
		return new File(dir, filename);
	}

	protected File createTarget() {
		File target = target();

		if (!FileUtils.createFile(target)) {
			throw new DownloadException("file create failed! file: " + target.getAbsolutePath());
		}
		return target;
	}

	protected void async(String threadName, ThrowingRunnable runnable, Consumer<Exception> onException) {
		executor.execute(() -> {
			Thread thread = Thread.currentThread();
			String oldName = thread.getName();
			thread.setName(threadName);
			try {
				runnable.run();
			}
			catch (Exception e) {
				onException.accept(e);
			}
			finally {
				thread.setName(oldName);
			}
		});
	}

	protected void upsertEx(Exception e) {
		if (e instanceof DownloadException de) {
			ex = de;
		}
		else {
			ex = new DownloadException("download error!", e);
		}
	}

}
