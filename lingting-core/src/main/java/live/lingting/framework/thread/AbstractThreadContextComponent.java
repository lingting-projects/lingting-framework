package live.lingting.framework.thread;

import live.lingting.framework.context.ContextComponent;
import live.lingting.framework.context.ContextHolder;
import live.lingting.framework.util.StringUtils;
import live.lingting.framework.util.ThreadUtils;
import live.lingting.framework.util.ValueUtils;
import live.lingting.framework.value.WaitValue;
import lombok.SneakyThrows;
import org.slf4j.Logger;

import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author lingting 2023-04-22 10:40
 */
@SuppressWarnings("java:S2142")
public abstract class AbstractThreadContextComponent implements ContextComponent {

	protected final Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

	protected final WaitValue<Thread> threadValue = WaitValue.of();

	protected void thread(Consumer<Thread> consumer) {
		thread(thread -> {
			consumer.accept(thread);
			return null;
		}, null);
	}

	protected <T> T thread(Function<Thread, T> function, T defaultValue) {
		if (!threadValue.isNull()) {
			Thread value = threadValue.getValue();
			return function.apply(value);
		}
		return defaultValue;
	}

	protected long threadId() {
		return thread(Thread::getId, -1L);
	}

	@SneakyThrows
	protected void interrupt() {
		thread(t -> {
			if (!t.isInterrupted()) {
				t.interrupt();
			}
		});
		threadValue.update((Thread) null);
	}

	protected void init() {

	}

	public boolean isRun() {
		boolean threadAvailable = thread(thread -> !thread.isInterrupted() && thread.isAlive(), false);
		return threadAvailable && !ContextHolder.isStop();
	}

	protected Executor executor() {
		return ThreadUtils.executor();
	}

	@Override
	public void onApplicationStart() {
		String name = getSimpleName();
		Executor executor = executor();
		executor.execute(new KeepRunnable(name) {
			@Override
			protected void process() throws Throwable {
				Thread thread = Thread.currentThread();
				threadValue.update(thread);
				try {
					AbstractThreadContextComponent.this.run();
				}
				finally {
					threadValue.update((Thread) null);
				}
			}
		});
	}

	@Override
	public void onApplicationStop() {
		log.warn("Class: {}; ThreadId: {}; closing!", getSimpleName(), threadId());
		interrupt();
	}

	public String getSimpleName() {
		String simpleName = getClass().getSimpleName();
		if (StringUtils.hasText(simpleName)) {
			return simpleName;
		}
		return getClass().getName();
	}

	public void run() {
		init();
		while (isRun()) {
			try {
				doRun();
			}
			catch (InterruptedException e) {
				interrupt();
				shutdown();
			}
			catch (Exception e) {
				error(e);
			}
		}
	}

	@SuppressWarnings("java:S112")
	protected abstract void doRun() throws Exception;

	/**
	 * 线程被中断触发.
	 */
	protected void shutdown() {
		log.warn("Class: {}; ThreadId: {}; shutdown!", getSimpleName(), threadId());
	}

	protected void error(Exception e) {
		log.error("Class: {}; ThreadId: {}; error!", getSimpleName(), threadId(), e);
	}

	public void awaitTerminated() {
		log.debug("wait thread terminated.");
		ValueUtils.awaitTrue(() -> thread(thread -> Thread.State.TERMINATED.equals(thread.getState()), true));
		log.debug("thread terminated.");
	}

}
