package live.lingting.framework.value

import java.math.BigInteger

/**
 * @author lingting 2023-12-19 10:58
 */
interface StepValue<T> : MutableIterator<T> {
    fun firt(): T?

    /**
     * 获取当前索引, 初始值为0
     */
    fun index(): BigInteger?

    /**
     * 重置索引为初始值
     */
    fun reset()

    /**
     * 复制一份当前数据
     */
    fun copy(): StepValue<T?>

    fun values(): List<T>

    /**
     * 返回下一个索引指向的元素
     */
    override fun next(): T
}
