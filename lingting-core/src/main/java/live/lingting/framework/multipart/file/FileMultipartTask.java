package live.lingting.framework.multipart.file;

import live.lingting.framework.multipart.Multipart;
import live.lingting.framework.multipart.MultipartTask;
import live.lingting.framework.thread.Async;

import static live.lingting.framework.multipart.file.FileMultipartTaskStatus.CANCELED;
import static live.lingting.framework.multipart.file.FileMultipartTaskStatus.MERGED;
import static live.lingting.framework.multipart.file.FileMultipartTaskStatus.RUNNING;
import static live.lingting.framework.multipart.file.FileMultipartTaskStatus.WAIT;

/**
 * @author lingting 2024-09-06 16:31
 */
public abstract class FileMultipartTask<I extends FileMultipartTask<I>> extends MultipartTask<I> {

	protected FileMultipartTaskStatus taskStatus = WAIT;

	protected FileMultipartTask(Multipart multipart) {
		super(multipart);
	}

	protected FileMultipartTask(Multipart multipart, Async async) {
		super(multipart, async);
	}

	@Override
	public boolean isCompleted() {
		return super.isCompleted() && (taskStatus == MERGED || taskStatus == CANCELED);
	}

	@Override
	protected void onStarted() {
		taskStatus = RUNNING;
	}

	@Override
	protected void onCompleted() {
		String id = getId();
		if (failedNumber > 0) {
			log.debug("[{}] onCancel", id);
			onCancel();
			log.debug("[{}] onCanceled", id);
			taskStatus = CANCELED;
		}
		else {
			log.debug("[{}] onMerge", id);
			onMerge();
			log.debug("[{}] onMerged", id);
			taskStatus = MERGED;
		}
		multipart.clear();
	}

	protected abstract void onMerge();

	protected abstract void onCancel();

	public FileMultipartTaskStatus getTaskStatus() {return this.taskStatus;}
}
