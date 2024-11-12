package live.lingting.framework.multipart;

import live.lingting.framework.lock.JavaReentrantLock;
import live.lingting.framework.thread.Async;
import live.lingting.framework.util.ValueUtils;
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
@SuppressWarnings({"unchecked", "java:S1172", "java:S1181", "java:S112"})
public abstract class MultipartTask<I extends MultipartTask<I>> {

	protected final Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

	protected final JavaReentrantLock lock = new JavaReentrantLock();

	protected final AtomicReference<MultipartTaskStatus> reference = new AtomicReference<>(WAIT);

	protected final Async async;

	protected final Multipart multipart;

	protected final int partCount;

	protected final List<PartTask> tasks;

	protected int completedNumber = 0;

	protected int successfulNumber = 0;

	protected int failedNumber = 0;

	protected long maxRetryCount = 0L;

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

	public boolean hasFailed() {
		return failedNumber > 0;
	}

	public List<PartTask> tasks() {
		return Collections.unmodifiableList(tasks);
	}

	public List<PartTask> tasksFailed() {
		return tasks.stream().filter(PartTask::isFailed).toList();
	}

	public I await() {
		return await(null);
	}

	public I await(Duration duration) {
		if (isStarted()) {
			ValueUtils.awaitTrue(duration, this::isCompleted);
		}
		return (I) this;
	}

	/**
	 * 计算以及更新数据
	 */

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
					log.debug("[{}] onCompleted", multipart.id);
					onCompleted();
				}
			}
		});
	}

	public I start() {
		if (isStarted() || !reference.compareAndSet(WAIT, RUNNING)) {
			return (I) this;
		}

		String id = multipart.id;
		String name = "Multipart-" + id;
		async.submit(name, () -> {
			log.debug("[{}] onStarted", id);
			onStarted();
			for (Part part : multipart.parts) {
				async.submit(name + "-" + part.getIndex(), () -> doPart(part));
			}
		});
		return (I) this;
	}

	protected void doPart(Part part) {
		String id = getId();
		Long index = part.getIndex();

		PartTask task = new PartTask(part);
		tasks.add(task);

		while (true) {
			task.status = PartTaskStatus.RUNNING;
			Throwable t = null;
			try {
				log.debug("[{}] onPart {}", id, index);
				onPart(part);
				log.debug("[{}] onPart completed {}", id, index);
				task.status = PartTaskStatus.SUCCESSFUL;
			}
			catch (Throwable throwable) {
				t = throwable;
				task.status = PartTaskStatus.FAILED;
			}

			task.t = t;
			if (task.isSuccessful() || !allowRetry(task, t)) {
				calculate();
				break;
			}
			task.retryCount += 1;
		}
	}

	protected boolean allowRetry(PartTask task, Throwable t) {
		return !isInterrupt(t) && task.getRetryCount() < maxRetryCount;
	}

	public boolean isInterrupt(Throwable throwable) {
		return throwable instanceof InterruptedException;
	}

	protected void onStarted() {
		//
	}

	protected abstract void onPart(Part part) throws Throwable;

	protected void onCompleted() {
		//
	}

	public String getId() {
		return getMultipart().getId();
	}

	public Multipart getMultipart() {return this.multipart;}

	public int getPartCount() {return this.partCount;}

	public int getCompletedNumber() {return this.completedNumber;}

	public int getSuccessfulNumber() {return this.successfulNumber;}

	public int getFailedNumber() {return this.failedNumber;}

	public long getMaxRetryCount() {return this.maxRetryCount;}

	public void setMaxRetryCount(long maxRetryCount) {this.maxRetryCount = maxRetryCount;}
}
