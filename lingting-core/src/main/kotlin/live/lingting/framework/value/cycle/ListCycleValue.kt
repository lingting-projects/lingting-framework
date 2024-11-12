package live.lingting.framework.value.cycle

import live.lingting.framework.util.CollectionUtils

/**
 * @author lingting 2024-04-29 15:45
 */
class ListCycleValue<T>(private val list: List<T>) : AbstractConcurrentCycleValue<T>() {
    private var index = -1

    val isEmpty: Boolean
        get() = CollectionUtils.isEmpty(list)

    override fun doNext(): T? {
        index += 1
        if (index < list.size) {
            return list[index]
        }
        doReset()
        return doNext()
    }

    override fun doReset() {
        index = -1
    }
}
