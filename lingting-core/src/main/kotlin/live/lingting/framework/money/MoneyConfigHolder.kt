package live.lingting.framework.money

import live.lingting.framework.thread.StackThreadLocal

/**
 * @author lingting 2023-05-07 18:00
 */
class MoneyConfigHolder private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        private val THREAD_LOCAL = StackThreadLocal<MoneyConfig>()

        fun get(): MoneyConfig? {
            return THREAD_LOCAL.get()
        }

        fun put(config: MoneyConfig?) {
            THREAD_LOCAL.put(config!!)
        }

        fun pop() {
            THREAD_LOCAL.pop()
        }
    }
}
