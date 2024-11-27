package live.lingting.framework.value.cycle

/**
 * @author lingting 2024-04-29 15:45
 */
class ListCycleValue<T>(private val list: List<T>) : AbstractConcurrentCycleValue<T>() {
    private var index = -1

    val isEmpty: Boolean
        get() = list.isNullOrEmpty()

    override fun doNext(): T {
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
