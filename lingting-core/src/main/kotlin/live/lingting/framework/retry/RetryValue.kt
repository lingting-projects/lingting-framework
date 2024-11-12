package live.lingting.framework.retry

/**
 * @param success 是否执行成功
 * @author lingting 2023-10-23 18:59
 */

data class RetryValue<T>(val value: T, val success: Boolean, val logs: List<RetryLog<T>>) {

    fun get(): T {
        if (success) {
            return value
        }
        throw logs.getLast().getException()
    }
}
