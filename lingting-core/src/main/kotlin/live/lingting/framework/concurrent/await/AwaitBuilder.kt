package live.lingting.framework.concurrent.await

import live.lingting.framework.concurrent.Await
import live.lingting.framework.thread.platform.PlatformThread
import live.lingting.framework.thread.virtual.VirtualThread
import live.lingting.framework.util.ThreadUtils
import live.lingting.framework.util.ValueUtils
import java.time.Duration
import java.time.temporal.TemporalUnit
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

/**
 * @author lingting 2025/5/13 13:53
 */
class AwaitBuilder<R> {

    companion object {

        private val atomic = AtomicLong()

        fun nextName() = "await-${atomic.andIncrement}"

        var defaultSleep = Runnable { Thread.sleep(50) }

        var defaultExecutor: Executor = ThreadUtils

    }

    var timeout: Duration? = null

    var worker: AwaitWorker<R>? = null

    var predicate: AwaitPredicate<R>? = null

    var name = nextName()

    /**
     * 下次调用 worker 前进行休眠
     */
    var sleep = defaultSleep

    var onTimeout = AwaitOnTimeout<R> { _, r -> r.interrupt() }

    var executor = defaultExecutor

    fun timeout(timeout: Duration?): AwaitBuilder<R> {
        this.timeout = timeout
        return this
    }

    fun timeout(amount: Long, unit: TemporalUnit) = timeout(Duration.of(amount, unit))

    fun timeout(amount: Long, unit: TimeUnit) = timeout(amount, unit.toChronoUnit())

    fun worker(worker: AwaitWorker<R>): AwaitBuilder<R> {
        this.worker = worker
        return this
    }

    fun predicate(predicate: AwaitPredicate<R>): AwaitBuilder<R> {
        this.predicate = predicate
        return this
    }

    fun worker(worker: AwaitWorker<R>, predicate: AwaitPredicate<R>) = worker(worker).also { predicate(predicate) }

    fun notNull(worker: AwaitWorker<R>) = worker(worker) { _, r -> r != null }

    fun isNull(worker: AwaitWorker<R>) = worker(worker) { _, r -> r == null }

    fun isPresent(worker: AwaitWorker<R>) = worker(worker) { _, r -> ValueUtils.isPresent(r) }

    fun isTrue(worker: AwaitWorker<R>) = worker(worker) { _, r -> r == true }

    fun isFalse(worker: AwaitWorker<R>) = worker(worker) { _, r -> r == false }

    fun name(name: String?): AwaitBuilder<R> {
        this.name = if (name.isNullOrBlank()) nextName() else name
        return this
    }

    fun sleep(runnable: Runnable): AwaitBuilder<R> {
        this.sleep = runnable
        return this
    }

    fun sleep(duration: Duration) = sleep { Thread.sleep(duration) }

    fun sleep(amount: Long, unit: TemporalUnit) = sleep(Duration.of(amount, unit))

    fun sleep(amount: Long, unit: TimeUnit) = sleep(amount, unit.toChronoUnit())

    fun onTimeout(onTimeout: AwaitOnTimeout<R>): AwaitBuilder<R> {
        this.onTimeout = onTimeout
        return this
    }

    fun interrupt() = onTimeout { _, r -> r.interrupt() }

    fun executor(executor: Executor): AwaitBuilder<R> {
        this.executor = executor
        return this
    }

    fun platform() = executor(PlatformThread)

    fun virtual() = executor(VirtualThread)

    fun build() = build(timeout)

    fun build(timeout: Duration?): Await<R> {
        val w = worker
        checkNotNull(w) { "worker must not null!" }
        val p = predicate
        checkNotNull(p) { "predicate must not null!" }
        return Await(timeout, w, p, name, sleep, onTimeout, executor)
    }

}
