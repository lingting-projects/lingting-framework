package live.lingting.framework.multipart.file

import live.lingting.framework.multipart.Multipart
import live.lingting.framework.multipart.MultipartTask
import live.lingting.framework.thread.Async

/**
 * @author lingting 2024-09-06 16:31
 */
abstract class FileMultipartTask<I : FileMultipartTask<I>> : MultipartTask<I> {
    var taskStatus: FileMultipartTaskStatus = FileMultipartTaskStatus.WAIT
        protected set

    protected constructor(multipart: Multipart) : super(multipart)

    protected constructor(multipart: Multipart, async: Async) : super(multipart, async)

    override val isCompleted: Boolean
        get() = super.isCompleted && (taskStatus == FileMultipartTaskStatus.MERGED || taskStatus == FileMultipartTaskStatus.CANCELED)

    override fun onStarted() {
        taskStatus = FileMultipartTaskStatus.RUNNING
    }

    override fun onCompleted() {
        val id = id
        if (failedNumber > 0) {
            log.debug("[{}] onCancel", id)
            onCancel()
            log.debug("[{}] onCanceled", id)
            taskStatus = FileMultipartTaskStatus.CANCELED
        } else {
            log.debug("[{}] onMerge", id)
            onMerge()
            log.debug("[{}] onMerged", id)
            taskStatus = FileMultipartTaskStatus.MERGED
        }
        multipart.clear()
    }

    protected abstract fun onMerge()

    protected abstract fun onCancel()
}
