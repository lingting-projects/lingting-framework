package live.lingting.framework.value.cycle

import live.lingting.framework.value.StepValue

/**
 * @author lingting 2024-01-23 15:22
 */
class StepCycleValue<T>(private val step: StepValue<T>) : AbstractConcurrentCycleValue<T>() {

    override fun doReset() {
        step.reset()
    }

    override fun doNext(): T {
        if (!step.hasNext()) {
            step.reset()
        }
        return step.next()
    }
}
