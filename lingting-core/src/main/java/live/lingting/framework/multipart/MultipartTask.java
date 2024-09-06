package live.lingting.framework.multipart;

import live.lingting.framework.lock.JavaReentrantLock;
import live.lingting.framework.thread.Async;
import live.lingting.framework.util.ValueUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static live.lingting.framework.multipart.MultipartTaskStatus.COMPLETED;
import static live.lingting.framework.multipart.MultipartTaskStatus.RUNNING;
import static live.lingting.framework.multipart.MultipartTaskStatus.WAIT;

/**
 * @author lingting 2024-09-05 14:48
 */
@SuppressWarnings({ "java:S1172", "java:S1181" })
public abstract class MultipartTask<I extends MultipartTask<I>> {

	protected final Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

	protected final JavaReentrantLock lock = new JavaReentrantLock();

	protected final AtomicReference<MultipartTaskStatus> reference = new AtomicReference<>(WAIT);

	protected final Async async;

	@Getter
	protected final Multipart multipart;

	@Getter
	protected final int partCount;

	protected final List<PartTask> tasks;

	@Getter
	protected int completedNumber = 0;

	@Getter
	protected int successfulNumber = 0;

	@Getter
	protected int failedNumber = 0;

	protected MultipartTask(Multipart multipart) {
		this(multipart, new Async());
	}

	protected MultipartTask(Multipart multipart, Async async) {
		this.async = async;
		this.multipart = multipart;
		this.partCount = multipart.parts.size();
		this.tasks = new CopyOnWriteArrayList<>();
	}

	public MultipartTaskStatus status() {
		return reference.get();
	}

	public boolean isStarted() {
		return WAIT != status();
	}

	public boolean isCompleted() {
		return COMPLETED == status();
	}

	public List<PartTask> tasks() {
		return Collections.unmodifiableList(tasks);
	}

	public void await() {
		await(null);
	}

	public void await(Duration duration) {
		if (!isStarted()) {
			return;
		}
		ValueUtils.awaitTrue(duration, this::isCompleted);
	}

	/**
	 * 计算以及更新数据
	 */
	@SneakyThrows
	protected void calculate() {
		lock.runByInterruptibly(() -> {
			AtomicInteger cn = new AtomicInteger();
			AtomicInteger sn = new AtomicInteger();
			AtomicInteger fn = new AtomicInteger();
			tasks.stream().filter(PartTask::isCompleted).forEach(t -> {
				cn.addAndGet(1);
				if (t.isSuccessful()) {
					sn.addAndGet(1);
				}
				else {
					fn.addAndGet(1);
				}
			});
			completedNumber = cn.get();
			successfulNumber = sn.get();
			failedNumber = fn.get();

			boolean isCompleted = completedNumber == partCount;
			boolean currentCompleted = isCompleted();
			if (isCompleted && !currentCompleted) {
				boolean isSet = reference.compareAndSet(RUNNING, COMPLETED);
				if (isSet) {
					onCompleted();
				}
			}
		});
	}

	public I start() {
		if (isStarted() || !reference.compareAndSet(WAIT, RUNNING)) {
			return (I) this;
		}

		async.submit("MultipartInit-" + multipart.id, () -> {
			onStarted();
			for (Part part : multipart.parts) {
				doPart(part);
			}
		});
		return (I) this;
	}

	protected void doPart(Part part) {
		PartTask task = new PartTask(part);
		tasks.add(task);
		while (true) {
			task.status = PartTaskStatus.RUNNING;
			Throwable t = null;
			try {
				onPart(part);
				task.status = PartTaskStatus.SUCCESSFUL;
			}
			catch (Throwable throwable) {
				t = throwable;
				task.status = PartTaskStatus.FAILED;
			}

			if (task.isSuccessful() || !allowRetry(task, t)) {
				calculate();
				break;
			}
			task.retryCount += 1;
		}
	}

	protected boolean allowRetry(PartTask task, Throwable t) {
		return false;
	}

	protected void onStarted() {
		//
	}

	protected abstract void onPart(Part part);

	protected void onCompleted() {
		//
	}

}
