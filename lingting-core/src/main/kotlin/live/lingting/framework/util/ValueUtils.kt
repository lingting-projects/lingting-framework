package live.lingting.framework.util

import live.lingting.framework.function.InterruptedRunnable
import live.lingting.framework.thread.Await
import live.lingting.framework.thread.VirtualThread
import java.lang.reflect.Array
import java.time.Duration
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.function.Predicate
import java.util.function.Supplier

/**
 * @author lingting 2024-01-26 15:47
 */
class ValueUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        /**
         * 默认使用虚拟线程
         */
        var executor: ExecutorService = VirtualThread.executor()

        fun awaitTrue(supplier: Supplier<Boolean>) {
            awaitTrue(null, supplier)
        }

        fun awaitTrue(timeout: Duration?, supplier: Supplier<Boolean>) {
            Await.builder<Boolean>(supplier, Predicate<Boolean> { obj: Boolean -> java.lang.Boolean.TRUE.equals(obj) }).timeout(timeout).executor(executor).await()
        }

        fun awaitFalse(supplier: Supplier<Boolean>) {
            awaitFalse(null, supplier)
        }

        fun awaitFalse(timeout: Duration?, supplier: Supplier<Boolean>) {
            Await.builder<Boolean>(supplier, Predicate<Boolean> { obj: Boolean -> java.lang.Boolean.FALSE.equals(obj) }).timeout(timeout).executor(executor).await()
        }

        /**
         * 等待值满足条件, 不满足条件休眠 500 毫秒
         *
         * @param supplier  值获取
         * @param predicate 值条件测试, 返回true表示该值为目标值, 当前函数会返回该值
         * @param <T>       值类型
         * @return 值
        </T> */
        fun <T> await(supplier: Supplier<T>, predicate: Predicate<T>): T {
            return await<T>(null, supplier, predicate, InterruptedRunnable.THREAD_SLEEP)
        }

        fun <T> await(timeout: Duration?, supplier: Supplier<T>, predicate: Predicate<T>): T {
            return await<T>(timeout, supplier, predicate, InterruptedRunnable.THREAD_SLEEP)
        }

        /**
         * 等待值满足条件
         *
         * @param supplier  值获取
         * @param predicate 值条件测试, 返回true表示该值为目标值, 当前函数会返回该值
         * @param sleep     休眠
         * @param <T>       值类型
         * @return 值
        </T> */
        fun <T> await(supplier: Supplier<T>, predicate: Predicate<T>, sleep: InterruptedRunnable): T {
            return await(null, supplier, predicate, sleep)
        }

        fun <T> await(
            timeout: Duration?, supplier: Supplier<T>, predicate: Predicate<T>,
            sleep: InterruptedRunnable
        ): T {
            return Await.builder<T>(supplier, predicate).sleep(sleep).timeout(timeout).executor(executor).await()
        }

        /**
         * 当前对象是否非null，且不为空
         *
         * @param value 值
         * @return boolean 不为空返回true
         */

        fun isPresent(value: Any?): Boolean {
            if (value == null) {
                return false
            }
            if (value is CharSequence) {
                return StringUtils.hasText(value)
            }
            if (value is Collection<*>) {
                return !value.isEmpty()
            }
            if (value is Map<*, *>) {
                return !value.isEmpty()
            }
            if (value.javaClass.isArray()) {
                return Array.getLength(value) > 0
            }
            return true
        }

        fun uuid(): String {
            return UUID.randomUUID().toString()
        }

        fun simpleUuid(): String {
            return uuid().replace("-", "")
        }

    }
}
