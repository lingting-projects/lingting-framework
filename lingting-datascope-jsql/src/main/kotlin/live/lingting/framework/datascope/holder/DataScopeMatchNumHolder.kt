package live.lingting.framework.datascope.holder

import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * DataScope 匹配数
 *
 * @author hccake
 */
class DataScopeMatchNumHolder private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        private val matchNumTreadLocal = ThreadLocal<Deque<AtomicInteger>>()

        /**
         * 每次 SQL 执行解析前初始化匹配次数为 0
         */
        @JvmStatic
        fun initMatchNum() {
            var deque = matchNumTreadLocal.get()
            if (deque == null) {
                deque = ArrayDeque()
                matchNumTreadLocal.set(deque)
            }
            deque.push(AtomicInteger())
        }

        /**
         * 获取当前 SQL 解析后被数据权限匹配中的次数
         *
         * @return int 次数
         */
        @JvmStatic
        fun pollMatchNum(): Int? {
            val deque = matchNumTreadLocal.get()
            val matchNum = deque.poll()
            return matchNum?.get()
        }

        /**
         * 如果存在计数器，则次数 +1
         */
        fun incrementMatchNumIfPresent() {
            val deque = matchNumTreadLocal.get()
            Optional.ofNullable(deque).map { obj: Deque<AtomicInteger> -> obj.peek() }.ifPresent { obj: AtomicInteger -> obj.incrementAndGet() }
        }

        /**
         * 删除 matchNumTreadLocal，在 SQL 执行解析后调用
         */
        @JvmStatic
        fun removeIfEmpty() {
            val deque = matchNumTreadLocal.get()
            if (deque == null || deque.isEmpty()) {
                matchNumTreadLocal.remove()
            }
        }
    }
}
