package live.lingting.framework.function

/**
 * @author lingting 2023/1/21 22:56
 */

fun interface ThrowingConsumer<T> : ThrowableConsumer<T> {

    override fun accept(t: T)
}
