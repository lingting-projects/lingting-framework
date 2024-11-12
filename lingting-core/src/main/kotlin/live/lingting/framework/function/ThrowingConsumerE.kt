package live.lingting.framework.function

/**
 * @author lingting 2023-12-22 11:49
 */
fun interface ThrowingConsumerE<T, E : Exception?> {

    fun accept(t: T)
}
