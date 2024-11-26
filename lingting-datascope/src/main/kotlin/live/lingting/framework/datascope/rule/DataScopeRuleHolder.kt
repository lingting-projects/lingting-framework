package live.lingting.framework.datascope.rule

import java.util.ArrayDeque
import java.util.Deque
import java.util.function.Supplier

/**
 * 数据范围规则的持有者，使用栈存储调用链中的数据范围规则
 * 区别于[DataScopeRule] [DataScopeRule] 是编程式数据范围控制的使用，优先级高于注解
 * @author hccake
 */
object DataScopeRuleHolder {
    /**
     * 使用栈存储 DataScopeRule，便于在方法嵌套调用时使用不同的数据范围控制。
     */
    private val LOCAL: ThreadLocal<Deque<DataScopeRule>> = ThreadLocal
        .withInitial(Supplier { ArrayDeque() })

    /**
     * 获取当前的 DataScopeRule 注解
     * @return DataScopeRule
     */
    @JvmStatic
    fun peek(): DataScopeRule? {
        val deque = LOCAL.get()
        return deque.peek()
    }

    /**
     * 入栈一个 DataScopeRule 注解
     * @return DataScopeRule
     */
    @JvmStatic
    fun push(rule: DataScopeRule): DataScopeRule {
        var deque = LOCAL.get()
        if (deque == null) {
            deque = ArrayDeque()
            LOCAL.set(deque)
        }
        deque.push(rule)
        return rule
    }

    /**
     * 弹出最顶部 DataScopeRule
     */
    @JvmStatic
    fun poll() {
        val deque = LOCAL.get()
        if (deque == null) {
            return
        }
        deque.poll()
        // 当没有元素时，清空 ThreadLocal
        if (deque.isEmpty()) {
            clear()
        }
    }

    /**
     * 清除 TreadLocal
     */
    @JvmStatic
    fun clear() {
        LOCAL.remove()
    }
}
