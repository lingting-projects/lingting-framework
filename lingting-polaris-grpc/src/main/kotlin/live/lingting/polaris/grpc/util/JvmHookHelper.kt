package live.lingting.polaris.grpc.util

/**
 * @author lixiaoshuang
 */
class JvmHookHelper private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        /**
         * Add JVM callback hooks.
         *
         * @param runnable Functional interface
         */
        fun addShutdownHook(runnable: Runnable?): Boolean {
            Runtime.getRuntime().addShutdownHook(Thread(runnable))
            return true
        }
    }
}
