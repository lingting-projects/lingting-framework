package live.lingting.framework.thread;

import live.lingting.framework.function.ThrowableRunnable;
import lombok.RequiredArgsConstructor;

/**
 * @author lingting 2024-04-29 10:41
 */
@RequiredArgsConstructor
public class StateKeepRunnable extends KeepRunnable {

	protected final ThrowableRunnable runnable;

	protected Thread thread;

	protected long start;

	protected long end;

	protected State state = State.WAIT;

	@Override
	protected void process() throws Throwable {
		start = System.currentTimeMillis();
		state = State.RUNNING;
		thread = Thread.currentThread();
		runnable.run();
	}

	@Override
	protected void onFinally() {
		end = System.currentTimeMillis();
		state = State.FINISH;
	}

	/**
	 * 执行时长, 单位: 毫秒
	 */
	public long time() {
		if (state == State.WAIT) {
			return 0;
		}
		if (state == State.FINISH) {
			return end - start;
		}
		return System.currentTimeMillis() - start;
	}

	/**
	 * 结束
	 */
	public void stop() {
		if (thread != null) {
			thread.interrupt();
		}
	}

	public enum State {

		WAIT,

		RUNNING,

		FINISH,

	}

}
