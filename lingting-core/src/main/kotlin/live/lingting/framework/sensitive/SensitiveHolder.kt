package live.lingting.framework.sensitive

/**
 * @author lingting 2023-06-30 17:57
 */
class SensitiveHolder private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        private val THREAD_LOCAL = ThreadLocal<Boolean>()

        fun allowSensitive(): Boolean {
            return java.lang.Boolean.FALSE != THREAD_LOCAL.get()
        }

        fun setSensitive(flag: Boolean) {
            THREAD_LOCAL.set(flag)
        }

        fun remove() {
            THREAD_LOCAL.remove()
        }
    }
}
