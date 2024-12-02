package live.lingting.framework.money

import live.lingting.framework.context.StackContext

/**
 * @author lingting 2023-05-07 18:00
 */
object MoneyConfigHolder {

    private val CONTEXT = StackContext<MoneyConfig?>()

    @JvmStatic
    fun get(): MoneyConfig? {
        return CONTEXT.peek()
    }

    @JvmStatic
    fun put(config: MoneyConfig?) {
        CONTEXT.push(config)
    }

    @JvmStatic
    fun pop() {
        CONTEXT.pop()
    }

}
