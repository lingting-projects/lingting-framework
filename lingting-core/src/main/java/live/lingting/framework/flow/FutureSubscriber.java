package live.lingting.framework.flow;

import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Flow;

/**
 * @author lingting 2024-05-07 17:06
 */
public abstract class FutureSubscriber<R, T> implements Flow.Subscriber<T> {

	private final CompletableFuture<R> future = new CompletableFuture<>();

	private final List<T> list = new ArrayList<>();

	@Override
	public void onSubscribe(Flow.Subscription subscription) {
		subscription.request(Long.MAX_VALUE);
	}

	@Override
	public void onNext(T item) {
		list.add(item);
	}

	@Override
	public void onError(Throwable throwable) {
		future.completeExceptionally(throwable);
	}

	@Override
	public void onComplete() {
		R r = convert(list);
		future.complete(r);
	}

	public abstract R convert(List<T> list);

	@SneakyThrows
	public R get() {
		try {
			return future.get();
		}
		catch (ExecutionException e) {
			throw e.getCause();
		}
	}

}
