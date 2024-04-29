package live.lingting.framework.thread;

/**
 * @author lingting 2024-04-29 10:41
 */
@SuppressWarnings("java:S112")
public abstract class StateKeepRunnable extends KeepRunnable {

	protected Thread thread;

	protected long start;

	protected long end;

	protected State state = State.WAIT;

	protected StateKeepRunnable() {
		super();
	}

	protected StateKeepRunnable(String name) {
		super(name);
	}

	@Override
	protected void process() throws Throwable {
		start = System.currentTimeMillis();
		state = State.RUNNING;
		thread = Thread.currentThread();
		doProcess();
	}

	protected abstract void doProcess() throws Throwable;

	@Override
	protected void onFinally() {
		end = System.currentTimeMillis();
		state = State.FINISH;
	}

	public boolean isFinish() {
		return state == State.FINISH;
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
		if (thread != null && !isFinish() && !thread.isInterrupted()) {
			thread.interrupt();
		}
	}

	public enum State {

		WAIT,

		RUNNING,

		FINISH,

	}

}
