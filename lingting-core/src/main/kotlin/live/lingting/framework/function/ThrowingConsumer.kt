package live.lingting.framework.function

/**
 * @author lingting 2023/1/21 22:56
 */
fun interface ThrowingConsumer<T> : ThrowableConsumer<T> {

    @Throws(Exception::class)
    override fun accept(t: T)

}
