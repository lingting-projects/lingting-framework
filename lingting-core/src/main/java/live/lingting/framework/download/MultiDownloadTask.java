package live.lingting.framework.download;

import live.lingting.framework.exception.DownloadException;
import live.lingting.framework.stream.RandomAccessFileOutputStream;
import live.lingting.framework.value.StepValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

@Getter
@RequiredArgsConstructor
public class MultiDownloadTask<D extends AbstractMultiDownload<D>> {

	protected final D download;

	protected final File target;

	protected final StepValue<Long> step;

	protected long count = 0;

	protected boolean finished = false;

	public void start() {
		Long size = download.getFileSize();
		long maxShardSize = download.getMaxShardSize();
		while (!finished) {
			Long next = next();
			if (next == null || next < 0) {
				stop();
				return;
			}

			// 起始值
			long start = next - maxShardSize;
			// 结束值
			long end = Math.min(start + maxShardSize, size) - 1;
			tryDownload(start, end);
			count++;
		}
	}

	protected void tryDownload(long start, long end) {
		try {
			doDownload(start, end);
		}
		catch (Exception e) {
			Long size = download.getFileSize();
			String message = String.format("multi download error! size: %s; range: %d-%d", size, start, end);
			throw new DownloadException(message, e);
		}
	}

	protected void doDownload(long start, long end) throws IOException {
		try (RandomAccessFileOutputStream output = new RandomAccessFileOutputStream(target)) {
			output.seek(start);
			download.write(output, start, end);
		}
	}

	protected Long next() {
		try {
			return step.next();
		}
		catch (NoSuchElementException e) {
			return null;
		}
	}

	public void stop() {
		finished = true;
	}

}
