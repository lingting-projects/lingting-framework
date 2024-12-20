package live.lingting.framework.value.cycle

import live.lingting.framework.value.step.IteratorStepValue

/**
 * @author lingting 2024-01-23 15:24
 */
class IteratorCycleValue<T>(private val step: IteratorStepValue<T>) : AbstractConcurrentCycleValue<T>() {

    constructor(iterable: Iterable<T>) : this(iterable.iterator())

    constructor(iterator: Iterator<T>) : this(IteratorStepValue<T>(iterator))

    override fun doReset() {
        step.reset()
    }

    override fun doNext(): T {
        if (!step.hasNext()) {
            step.reset()
        }
        return step.next()
    }

    /**
     * 移除上一个next返回的元素
     */
    fun remove() {
        step.remove()
    }

}
