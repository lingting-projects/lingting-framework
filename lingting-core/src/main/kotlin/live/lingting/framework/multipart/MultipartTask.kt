package live.lingting.framework.multipart

import live.lingting.framework.kt.logger
import live.lingting.framework.lock.JavaReentrantLock
import live.lingting.framework.thread.Async
import live.lingting.framework.util.ValueUtils
import java.time.Duration
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Supplier

/**
 * @author lingting 2024-09-05 14:48
 */
abstract class MultipartTask<I : MultipartTask<I>> protected constructor(val multipart: Multipart, protected val async: Async = Async()) {

    protected val log = logger()

    protected val lock: JavaReentrantLock = JavaReentrantLock()

    protected val reference: AtomicReference<MultipartTaskStatus> = AtomicReference(MultipartTaskStatus.WAIT)

    val partCount: Int = multipart.parts.size

    protected val tasks: MutableList<PartTask> = CopyOnWriteArrayList()

    var completedNumber: Int = 0
        protected set

    var successfulNumber: Int = 0
        protected set

    var failedNumber: Int = 0
        protected set


    var maxRetryCount: Long = 0L

    fun status(): MultipartTaskStatus {
        return reference.get()
    }

    val isStarted: Boolean
        get() = MultipartTaskStatus.WAIT != status()

    open val isCompleted: Boolean
        get() = MultipartTaskStatus.COMPLETED == status()

    fun hasFailed(): Boolean {
        return failedNumber > 0
    }

    fun tasks(): List<PartTask> {
        return Collections.unmodifiableList(tasks)
    }

    fun tasksFailed(): List<PartTask> {
        return tasks.stream().filter { obj: PartTask -> obj.isFailed }.toList()
    }


    fun await(duration: Duration? = null): I {
        if (isStarted) {
            ValueUtils.awaitTrue(duration, Supplier<Boolean> { this.isCompleted })
        }
        return this as I
    }

    /**
     * 计算以及更新数据
     */
    protected fun calculate() {
        lock.runByInterruptibly {
            val cn = AtomicInteger()
            val sn = AtomicInteger()
            val fn = AtomicInteger()
            tasks.stream().filter { obj: PartTask -> obj.isCompleted }.forEach { t: PartTask ->
                cn.addAndGet(1)
                if (t.isSuccessful) {
                    sn.addAndGet(1)
                } else {
                    fn.addAndGet(1)
                }
            }
            completedNumber = cn.get()
            successfulNumber = sn.get()
            failedNumber = fn.get()

            val isCompleted = completedNumber == partCount
            val currentCompleted = this.isCompleted
            if (isCompleted && !currentCompleted) {
                val isSet = reference.compareAndSet(MultipartTaskStatus.RUNNING, MultipartTaskStatus.COMPLETED)
                if (isSet) {
                    log.debug("[{}] onCompleted", multipart.id)
                    onCompleted()
                }
            }
        }
    }

    fun start(): I {
        if (isStarted || !reference.compareAndSet(MultipartTaskStatus.WAIT, MultipartTaskStatus.RUNNING)) {
            return this as I
        }

        val id = multipart.id
        val name = "Multipart-$id"
        async.submit(name) {
            log.debug("[{}] onStarted", id)
            onStarted()
            for (part in multipart.parts) {
                async.submit(name + "-" + part.index) { doPart(part!!) }
            }
        }
        return this as I
    }

    protected fun doPart(part: Part) {
        val id = id
        val index = part.index

        val task = PartTask(part)
        tasks.add(task)

        while (true) {
            task.status = PartTaskStatus.RUNNING
            var t: Throwable? = null
            try {
                log.debug("[{}] onPart {}", id, index)
                onPart(part)
                log.debug("[{}] onPart completed {}", id, index)
                task.status = PartTaskStatus.SUCCESSFUL
            } catch (throwable: Throwable) {
                t = throwable
                task.status = PartTaskStatus.FAILED
            }

            task.t = t
            if (task.isSuccessful || !allowRetry(task, t)) {
                calculate()
                break
            }
            task.retryCount += 1
        }
    }

    protected open fun allowRetry(task: PartTask, t: Throwable?): Boolean {
        return !isInterrupt(t) && task.retryCount < maxRetryCount
    }

    fun isInterrupt(throwable: Throwable?): Boolean {
        return throwable is InterruptedException
    }

    protected open fun onStarted() {
        //
    }


    protected abstract fun onPart(part: Part)

    protected open fun onCompleted() {
        //
    }

    val id: String
        get() = multipart.id
}
