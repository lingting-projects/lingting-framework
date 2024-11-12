package live.lingting.framework.queue;

import live.lingting.framework.lock.JavaReentrantLock;
import live.lingting.framework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 循环队列
 *
 * @author lingting 2023-05-30 10:25
 */
public class CircularQueue<T> {

	private final JavaReentrantLock lock = new JavaReentrantLock();

	private final List<T> source = new ArrayList<>();

	private Iterator<T> iterator;

	public CircularQueue<T> add(T t) {
		source.add(t);
		return this;
	}

	public CircularQueue<T> addAll(Collection<T> collection) {
		source.addAll(collection);
		return this;
	}

	public T pool() throws InterruptedException {
		return lock.getByInterruptibly(() -> {
			if (CollectionUtils.isEmpty(source)) {
				return null;
			}
			if (iterator == null || !iterator.hasNext()) {
				iterator = source.iterator();
			}

			return iterator.next();
		});
	}

}
