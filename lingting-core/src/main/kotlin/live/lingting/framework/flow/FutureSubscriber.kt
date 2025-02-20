package live.lingting.framework.flow

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.Flow

/**
 * @author lingting 2024-05-07 17:06
 */
abstract class FutureSubscriber<R, T> : Flow.Subscriber<T> {
    private val future = CompletableFuture<R>()

    private val list = ArrayList<T>()

    override fun onSubscribe(subscription: Flow.Subscription) {
        subscription.request(Long.MAX_VALUE)
    }

    override fun onNext(item: T) {
        list.add(item)
    }

    override fun onError(throwable: Throwable) {
        future.completeExceptionally(throwable)
    }

    override fun onComplete() {
        val r = convert(list)
        future.complete(r)
    }

    abstract fun convert(list: List<T>?): R

    fun get(): R {
        try {
            return future.get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }
}
