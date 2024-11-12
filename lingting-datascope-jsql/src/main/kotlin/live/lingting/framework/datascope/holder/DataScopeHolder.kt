package live.lingting.framework.datascope.holder

import live.lingting.framework.datascope.JsqlDataScope
import java.util.*
import java.util.function.Supplier
import kotlin.collections.ArrayDeque

class DataScopeHolder private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        /**
         * 使用栈存储 List<DataScope>，便于在方法嵌套调用时使用不同的数据权限控制。
        </DataScope> */
        private val DATA_SCOPES: ThreadLocal<Deque<List<JsqlDataScope>>> = ThreadLocal.withInitial(Supplier { ArrayDeque() })

        /**
         * 获取当前的 dataScopes
         *
         * @return List<DataScope>
        </DataScope> */
        fun peek(): List<JsqlDataScope?>? {
            val deque = DATA_SCOPES.get()
            return if (deque == null) ArrayList() else deque.peek()
        }

        /**
         * 入栈一组 dataScopes
         */
        fun push(dataScopes: List<JsqlDataScope>) {
            var deque = DATA_SCOPES.get()
            if (deque == null) {
                deque = java.util.ArrayDeque()
            }
            deque.push(dataScopes)
        }

        /**
         * 弹出最顶部 dataScopes
         */
        fun poll() {
            val deque = DATA_SCOPES.get()
            deque.poll()
            // 当没有元素时，清空 ThreadLocal
            if (deque.isEmpty()) {
                clear()
            }
        }

        /**
         * 清除 TreadLocal
         */
        private fun clear() {
            DATA_SCOPES.remove()
        }
    }
}
