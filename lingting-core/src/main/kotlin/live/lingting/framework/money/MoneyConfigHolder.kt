package live.lingting.framework.money

import live.lingting.framework.thread.StackThreadLocal

/**
 * @author lingting 2023-05-07 18:00
 */
object MoneyConfigHolder {
    private val THREAD_LOCAL = StackThreadLocal<MoneyConfig>()

    @JvmStatic
    fun get(): MoneyConfig? {
        return THREAD_LOCAL.get()
    }

    @JvmStatic
    fun put(config: MoneyConfig?) {
        THREAD_LOCAL.put(config!!)
    }

    @JvmStatic
    fun pop() {
        THREAD_LOCAL.pop()
    }
}
