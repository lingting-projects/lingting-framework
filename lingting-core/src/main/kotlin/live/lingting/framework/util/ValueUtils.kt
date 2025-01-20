package live.lingting.framework.util

import java.lang.reflect.Array
import java.time.Duration
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeoutException
import java.util.function.Predicate
import java.util.function.Supplier
import live.lingting.framework.function.InterruptedRunnable
import live.lingting.framework.thread.Await
import live.lingting.framework.thread.VirtualThread

/**
 * @author lingting 2024-01-26 15:47
 */
object ValueUtils {
    /**
     * 默认使用虚拟线程
     */
    @JvmStatic
    var executor: ExecutorService = VirtualThread.executor()

    @JvmStatic
    @Throws(TimeoutException::class)
    fun awaitTrue(supplier: Supplier<Boolean>) {
        awaitTrue(null, supplier)
    }

    @JvmStatic
    @Throws(TimeoutException::class)
    fun awaitTrue(timeout: Duration?, supplier: Supplier<Boolean>) {
        Await.builder<Boolean>(supplier, Predicate<Boolean> { obj -> obj == true }).timeout(timeout).executor(executor).await()
    }

    @JvmStatic
    @Throws(TimeoutException::class)
    fun awaitFalse(supplier: Supplier<Boolean>) {
        awaitFalse(null, supplier)
    }

    @JvmStatic
    @Throws(TimeoutException::class)
    fun awaitFalse(timeout: Duration?, supplier: Supplier<Boolean>) {
        Await.builder<Boolean>(supplier, Predicate<Boolean> { obj -> obj == false }).timeout(timeout).executor(executor).await()
    }

    /**
     * 等待值满足条件, 不满足条件休眠 500 毫秒
     * @param supplier  值获取
     * @param predicate 值条件测试, 返回true表示该值为目标值, 当前函数会返回该值
     * @param <T>       值类型
     * @return 值
    </T> */
    @JvmStatic
    @Throws(TimeoutException::class)
    fun <T> await(supplier: Supplier<T>, predicate: Predicate<T>): T {
        return await<T>(null, supplier, predicate, InterruptedRunnable.THREAD_SLEEP)
    }

    @JvmStatic
    @Throws(TimeoutException::class)
    fun <T> await(timeout: Duration?, supplier: Supplier<T>, predicate: Predicate<T>): T {
        return await<T>(timeout, supplier, predicate, InterruptedRunnable.THREAD_SLEEP)
    }

    /**
     * 等待值满足条件
     * @param supplier  值获取
     * @param predicate 值条件测试, 返回true表示该值为目标值, 当前函数会返回该值
     * @param sleep     休眠
     * @param <T>       值类型
     * @return 值
    </T> */
    @JvmStatic
    @Throws(TimeoutException::class)
    fun <T> await(supplier: Supplier<T>, predicate: Predicate<T>, sleep: InterruptedRunnable): T {
        return await(null, supplier, predicate, sleep)
    }

    @JvmStatic
    @Throws(TimeoutException::class)
    fun <T> await(
        timeout: Duration?, supplier: Supplier<T>, predicate: Predicate<T>,
        sleep: InterruptedRunnable
    ): T {
        return Await.builder<T>(supplier, predicate).sleep(sleep).timeout(timeout).executor(executor).await()
    }

    /**
     * 当前对象是否非null，且不为空
     * @param value 值
     * @return boolean 不为空返回true
     */

    @JvmStatic
    @Throws(TimeoutException::class)
    fun isPresent(value: Any?): Boolean {
        if (value == null) {
            return false
        }
        if (value is CharSequence) {
            return StringUtils.hasText(value)
        }
        if (value is Collection<*>) {
            return value.isNotEmpty()
        }
        if (value is Map<*, *>) {
            return value.isNotEmpty()
        }
        if (value.javaClass.isArray) {
            return Array.getLength(value) > 0
        }
        return true
    }

    @JvmStatic
    @Throws(TimeoutException::class)
    fun uuid(): String {
        return UUID.randomUUID().toString()
    }

    @JvmStatic
    @Throws(TimeoutException::class)
    fun simpleUuid(): String {
        return uuid().replace("-", "")
    }

}

