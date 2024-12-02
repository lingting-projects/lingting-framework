package live.lingting.framework.datascope.rule

import live.lingting.framework.context.DequeContext

/**
 * 数据范围规则的持有者，使用栈存储调用链中的数据范围规则
 * 区别于[DataScopeRule] [DataScopeRule] 是编程式数据范围控制的使用，优先级高于注解
 * @author hccake
 */
object DataScopeRuleHolder {
    /**
     * 使用栈存储 DataScopeRule，便于在方法嵌套调用时使用不同的数据范围控制。
     */
    private val CONTEXT = DequeContext<DataScopeRule?>()

    /**
     * 获取当前的 DataScopeRule 注解
     * @return DataScopeRule
     */
    @JvmStatic
    fun peek(): DataScopeRule? {
        return CONTEXT.peek()
    }

    /**
     * 入栈一个 DataScopeRule 注解
     * @return DataScopeRule
     */
    @JvmStatic
    fun push(rule: DataScopeRule?) {
        CONTEXT.push(rule)
    }

    /**
     * 弹出最顶部 DataScopeRule
     */
    @JvmStatic
    fun poll() {
        val deque = CONTEXT.get()
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
        CONTEXT.remove()
    }
}
