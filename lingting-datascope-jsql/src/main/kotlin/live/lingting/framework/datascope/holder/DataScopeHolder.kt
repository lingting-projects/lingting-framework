package live.lingting.framework.datascope.holder

import java.util.ArrayDeque
import java.util.Deque
import java.util.function.Supplier
import live.lingting.framework.datascope.JsqlDataScope

object DataScopeHolder {
    /**
     * 使用栈存储 List<DataScope>，便于在方法嵌套调用时使用不同的数据权限控制。
    </DataScope> */
    private val DATA_SCOPES: ThreadLocal<Deque<List<JsqlDataScope>>> = ThreadLocal.withInitial(Supplier { ArrayDeque() })

    /**
     * 获取当前的 dataScopes
     *
     * @return List<DataScope>
    </DataScope> */
    @JvmStatic
    fun peek(): List<JsqlDataScope> {
        val deque = DATA_SCOPES.get()
        return if (deque == null) ArrayList() else deque.peek()
    }

    /**
     * 入栈一组 dataScopes
     */
    @JvmStatic
    fun push(dataScopes: List<JsqlDataScope>) {
        var deque = DATA_SCOPES.get()
        if (deque == null) {
            deque = ArrayDeque()
            DATA_SCOPES.set(deque)
        }
        deque.push(dataScopes)
    }

    /**
     * 弹出最顶部 dataScopes
     */
    @JvmStatic
    fun poll() {
        val deque = DATA_SCOPES.get()
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
    private fun clear() {
        DATA_SCOPES.remove()
    }
}
