package live.lingting.framework.multipart

/**
 * @author lingting 2024-09-05 14:48
 */
class PartTask(val part: Part) {

    var t: Throwable? = null

    var status: PartTaskStatus = PartTaskStatus.WAIT

    var retryCount: Long = 0L

    val isCompleted: Boolean
        get() = isSuccessful || isFailed

    val isSuccessful: Boolean
        get() = status == PartTaskStatus.SUCCESSFUL

    val isFailed: Boolean
        get() = status == PartTaskStatus.FAILED
}
