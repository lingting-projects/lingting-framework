package live.lingting.framework.datascope.holder

import live.lingting.framework.datascope.annotation.DataPermission
import live.lingting.framework.datascope.handler.DataPermissionRule
import java.util.*
import java.util.function.Supplier
import kotlin.collections.ArrayDeque

/**
 * 数据权限规则的持有者，使用栈存储调用链中的数据权限规则
 *
 *
 * 区别于[DataPermission] [DataPermissionRule] 是编程式数据权限控制的使用，优先级高于注解
 *
 * @author hccake
 */
class DataPermissionRuleHolder private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        /**
         * 使用栈存储 DataPermissionRule，便于在方法嵌套调用时使用不同的数据权限控制。
         */
        private val DATA_PERMISSION_RULES: ThreadLocal<Deque<DataPermissionRule>> = ThreadLocal
            .withInitial(Supplier { ArrayDeque() })

        /**
         * 获取当前的 DataPermissionRule 注解
         *
         * @return DataPermissionRule
         */
        fun peek(): DataPermissionRule? {
            val deque = DATA_PERMISSION_RULES.get()
            return deque?.peek()
        }

        /**
         * 入栈一个 DataPermissionRule 注解
         *
         * @return DataPermissionRule
         */
        fun push(dataPermissionRule: DataPermissionRule): DataPermissionRule {
            var deque = DATA_PERMISSION_RULES.get()
            if (deque == null) {
                deque = java.util.ArrayDeque()
            }
            deque.push(dataPermissionRule)
            return dataPermissionRule
        }

        /**
         * 弹出最顶部 DataPermissionRule
         */
        fun poll() {
            val deque = DATA_PERMISSION_RULES.get()
            deque.poll()
            // 当没有元素时，清空 ThreadLocal
            if (deque.isEmpty()) {
                clear()
            }
        }

        /**
         * 清除 TreadLocal
         */
        fun clear() {
            DATA_PERMISSION_RULES.remove()
        }
    }
}
