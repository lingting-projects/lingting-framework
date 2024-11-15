package live.lingting.framework.retry

/**
 * @author lingting 2023-10-23 19:15
 */
data class RetryLog<T>(val value: T?, val ex: Throwable?)
