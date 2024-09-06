package live.lingting.framework.download;

import live.lingting.framework.function.ThrowingFunction;
import live.lingting.framework.multipart.Multipart;
import live.lingting.framework.multipart.Part;
import live.lingting.framework.multipart.PartTask;
import live.lingting.framework.multipart.file.FileMultipartTask;
import live.lingting.framework.stream.RandomAccessOutputStream;
import live.lingting.framework.thread.Async;
import live.lingting.framework.util.StreamUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;

/**
 * @author lingting 2024-09-06 16:55
 */
@Slf4j
public class DownloadFileMultipartTask extends FileMultipartTask<DownloadFileMultipartTask> {

	protected final File target;

	protected final ThrowingFunction<Part, InputStream> download;

	protected final long maxRetryCount;

	protected DownloadFileMultipartTask(Multipart multipart, long maxRetryCount,
			ThrowingFunction<Part, InputStream> download, File target) {
		this(multipart, maxRetryCount, new Async(), target, download);
	}

	protected DownloadFileMultipartTask(Multipart multipart, long maxRetryCount, Async async, File target,
			ThrowingFunction<Part, InputStream> download) {
		super(multipart, async);
		this.maxRetryCount = maxRetryCount;
		this.target = target;
		this.download = download;
	}

	@Override
	protected void onMerge() {
		//
	}

	@Override
	protected void onCancel() {
		//
	}

	@Override
	protected void onPart(Part part) throws Throwable {
		try (RandomAccessOutputStream output = new RandomAccessOutputStream(target)) {
			output.seek(part.getStart());
			try (InputStream input = download.apply(part)) {
				StreamUtils.write(input, output);
			}
		}
	}

	@Override
	protected boolean allowRetry(PartTask task, Throwable t) {
		return task.getRetryCount() < maxRetryCount;
	}

}
