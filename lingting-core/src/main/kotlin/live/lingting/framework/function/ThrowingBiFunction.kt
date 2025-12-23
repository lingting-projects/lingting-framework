package live.lingting.framework.function

/**
 * @author lingting 2023/2/2 17:36
 */
fun interface ThrowingBiFunction<T, D, R> {

    @Throws(Exception::class)
    fun apply(t: T, d: D): R

}
