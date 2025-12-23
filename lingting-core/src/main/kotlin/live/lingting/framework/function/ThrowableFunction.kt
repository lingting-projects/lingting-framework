package live.lingting.framework.function

/**
 * @author lingting 2023/1/16 17:46
 */
fun interface ThrowableFunction<T, R> {

    @Throws(Throwable::class)
    fun apply(t: T): R

}
