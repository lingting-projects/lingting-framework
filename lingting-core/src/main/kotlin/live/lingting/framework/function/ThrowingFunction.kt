package live.lingting.framework.function

/**
 * @author lingting 2023/2/2 17:36
 */
fun interface ThrowingFunction<T, R> : ThrowableFunction<T, R> {

    @Throws(Exception::class)
    override fun apply(t: T): R

}
