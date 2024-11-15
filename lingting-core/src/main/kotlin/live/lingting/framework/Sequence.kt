package live.lingting.framework

import live.lingting.framework.util.ClassUtils
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import java.util.*

/**
 * 排序用
 *
 * @author lingting 2024-01-30 11:15
 */
interface Sequence {
    val sequence: Int

    class SequenceComparator(private val isAsc: Boolean, private val defaultSequence: Int) : Comparator<Any?> {
        override fun compare(o1: Any?, o2: Any?): Int {
            val i1 = find(o1)
            val i2 = find(o2)

            if (i1 == i2) {
                return 0
            }

            val isLeft = if (isAsc) i1 < i2 else i1 > i2
            // 是否o1排o2前面
            return if (isLeft) -1 else 1
        }

        /**
         * 获取当前排序规则内, 最低优先级的值
         */
        protected fun lowerSequence(): Int {
            return if (isAsc) Int.MAX_VALUE else Int.MIN_VALUE
        }

        /**
         * 返回排序在前面的优先级
         */
        protected fun high(o1: Int?, o2: Int?): Int {
            if (o1 == null && o2 == null) {
                return defaultSequence
            }

            if (o1 == null) {
                return o2!!
            }

            if (o2 == null) {
                return o1
            }

            return if (isAsc) min(o1, o2) else max(o1, o2)
        }

        protected fun find(obj: Any?): Int {
            if (obj == null) {
                return defaultSequence
            }

            val orderSequence = if (obj is Sequence) obj.sequence else null
            val orderSpring = findBySpring(obj)
            return high(orderSequence, orderSpring)
        }

        protected fun findBySpring(obj: Any): Int? {
            if (!ClassUtils.isPresent("org.springframework.core.annotation.Order", javaClass.getClassLoader())) {
                return null
            }
            val annotation: Order = obj.javaClass.getAnnotation<Order>(Order::class.java)
            // 注解上的排序值
            val oa = if (annotation != null) annotation.value else null
            // 类方法上的排序值
            val om = if (obj is Ordered) obj.order else null
            // 均为null则返回null
            if (oa == null && om == null) {
                return null
            }
            return high(oa, om)
        }
    }

    companion object {
        fun <T> asc(list: List<T>) {
            list.sort(INSTANCE_ASC)
        }

        fun <T> asc(collection: Collection<T>): List<T> {
            return collection.stream().sorted(INSTANCE_ASC).toList()
        }

        fun <T> desc(list: List<T>) {
            list.sort(INSTANCE_DESC)
        }

        fun <T> desc(collection: Collection<T>): List<T> {
            return collection.stream().sorted(INSTANCE_DESC).toList()
        }

        val INSTANCE_ASC: SequenceComparator = SequenceComparator(true, 0)

        val INSTANCE_DESC: SequenceComparator = SequenceComparator(false, 0)
    }
}