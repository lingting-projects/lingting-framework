package live.lingting.framework.datascope.rule

import java.util.ArrayDeque
import java.util.Deque
import java.util.function.Supplier
import live.lingting.framework.datascope.annotation.DataPermission

/**
 * 数据权限规则的持有者，使用栈存储调用链中的数据权限规则
 *
 *
 * 区别于[DataPermission] [DataPermissionRule] 是编程式数据权限控制的使用，优先级高于注解
 *
 * @author hccake
 */
object DataPermissionRuleHolder {
    /**
     * 使用栈存储 DataPermissionRule，便于在方法嵌套调用时使用不同的数据权限控制。
     */
    private val LOCAL: ThreadLocal<Deque<DataPermissionRule>> = ThreadLocal
        .withInitial(Supplier { ArrayDeque() })

    /**
     * 获取当前的 DataPermissionRule 注解
     *
     * @return DataPermissionRule
     */
    @JvmStatic
    fun peek(): DataPermissionRule? {
        val deque = LOCAL.get()
        return deque.peek()
    }

    /**
     * 入栈一个 DataPermissionRule 注解
     *
     * @return DataPermissionRule
     */
    @JvmStatic
    fun push(rule: DataPermissionRule): DataPermissionRule {
        var deque = LOCAL.get()
        if (deque == null) {
            deque = ArrayDeque()
            LOCAL.set(deque)
        }
        deque.push(rule)
        return rule
    }

    /**
     * 弹出最顶部 DataPermissionRule
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
