package live.lingting.framework.value;

import live.lingting.framework.lock.JavaReentrantLock;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * @author lingting 2024-09-28 15:29
 */
@RequiredArgsConstructor
public class LazyValue<T> {

	protected final JavaReentrantLock lock = new JavaReentrantLock();

	protected final AtomicBoolean first = new AtomicBoolean(true);

	protected T t;

	protected final Supplier<T> supplier;

	@SneakyThrows
	public T get() {
		if (!isFirst()) {
			return t;
		}

		t = lock.getByInterruptibly(() -> {
			// 非首次进入锁
			if (!first.compareAndSet(true, false)) {
				return t;
			}
			// 首次进入时初始化
			return supplier.get();
		});
		return t;
	}

	public boolean isFirst() {
		return first.get();
	}

}
