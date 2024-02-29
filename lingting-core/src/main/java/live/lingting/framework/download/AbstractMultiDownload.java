package live.lingting.framework.download;

import live.lingting.framework.util.StreamUtils;
import live.lingting.framework.util.ValueUtils;
import live.lingting.framework.value.StepValue;
import live.lingting.framework.value.step.LongStepValue;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lingting 2024-01-16 20:23
 */
@Getter
@SuppressWarnings("unchecked")
public abstract class AbstractMultiDownload<D extends AbstractMultiDownload<D>> extends AbstractDownload<D> {

	/**
	 * 最大启动线程数
	 */
	protected final int maxThreadCount;

	/**
	 * 每个分片的最大大小, 单位: bytes
	 */
	protected final long maxShardSize;

	/**
	 * 文件大小, 用于多线程下载时进行分片. 单位: bytes
	 * <p>
	 * 设置为null或者小于1时自动解析文件具体大小
	 * </p>
	 */
	protected Long fileSize;

	protected long maxShard;

	protected long finishedShard;

	protected <B extends AbstractDownloadBuilder<B>> AbstractMultiDownload(B builder) {
		super(builder);
		this.maxThreadCount = builder.maxThreadCount;
		this.maxShardSize = builder.maxShardSize;
	}

	@Override
	protected void doStart() throws IOException {
		// 文件大小
		long size = generateFileSize();
		// 创建文件
		File target = createTarget();
		// 创建为一个随机访问文件, 并且设置大小
		try (RandomAccessFile file = new RandomAccessFile(target, "rw")) {
			StreamUtils.close(file);
		}

		// 最大分片数
		maxShard = size % maxShardSize == 0 ? size / maxShardSize : size / maxShardSize + 1;
		// 线程数
		long count = Math.min(maxShard, maxThreadCount);

		StepValue<Long> step = new LongStepValue(maxShardSize, null, maxShardSize * maxShard);

		List<MultiDownloadTask<D>> tasks = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			final int index = i;
			MultiDownloadTask<D> task = new MultiDownloadTask<>((D) this, target, step);
			async(String.format("DOWNLOAD-%d-%s", index, filename), task::start, e -> {
				log.error("download! index: {}", index, e);
				task.stop();
			});
			tasks.add(task);
		}

		ValueUtils.await(() -> {
			long finishedTask = 0;
			long currentFinishedShard = 0;

			for (MultiDownloadTask<D> task : tasks) {
				if (task.isFinished()) {
					// 更新已完成任务数
					finishedTask++;
				}
				// 如果发生异常但是还是运行中, 结束
				else if (ex != null) {
					task.stop();
				}
				// 更新已下载完成分片数
				currentFinishedShard += task.getCount();
				// 如果发生异常, 中断
			}
			finishedShard = currentFinishedShard;

			return tasks.size() - finishedTask;
		}, i -> i < 1);
		finished = true;
	}

	protected long generateFileSize() {
		if (fileSize != null) {
			return fileSize;
		}

		fileSize = remoteSize();
		return fileSize;
	}

	/**
	 * 从远端获取文件大小
	 */
	protected abstract long remoteSize();

	/**
	 * 把指定范围的文件流写入到输出流
	 * @param output 输出流
	 * @param start 索引起始
	 * @param end 索引结束
	 */
	protected abstract void write(OutputStream output, long start, long end) throws IOException;

}
