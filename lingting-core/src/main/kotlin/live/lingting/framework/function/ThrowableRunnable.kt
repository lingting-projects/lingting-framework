package live.lingting.framework.function

/**
 * @author lingting 2023/1/16 17:46
 */
fun interface ThrowableRunnable {

    @Throws(Throwable::class)
    fun run()

}
