package live.lingting.framework.function

/**
 * @author lingting 2023/1/21 22:56
 */
fun interface ThrowingBiConsumer<T, D> {

    fun accept(t: T, d: D)
}
