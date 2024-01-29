package live.lingting.framework.thread;

import live.lingting.framework.context.ContextComponent;
import live.lingting.framework.context.ContextHolder;
import live.lingting.framework.util.StringUtils;
import live.lingting.framework.util.ValueUtils;
import org.slf4j.Logger;

/**
 * @author lingting 2023-04-22 10:40
 */
public abstract class AbstractThreadContextComponent extends Thread implements ContextComponent {

	protected final Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

	protected void init() {

	}

	public boolean isRun() {
		return !isInterrupted() && isAlive() && !ContextHolder.isStop();
	}

	@Override
	public void onApplicationStart() {
		setName(getSimpleName());
		if (!isAlive()) {
			start();
		}
	}

	@Override
	public void onApplicationStop() {
		log.warn("Class: {}; ThreadId: {}; closing!", getSimpleName(), getId());
		interrupt();
	}

	public String getSimpleName() {
		String simpleName = getClass().getSimpleName();
		if (StringUtils.hasText(simpleName)) {
			return simpleName;
		}
		return getClass().getName();
	}

	@Override
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
		log.warn("Class: {}; ThreadId: {}; shutdown!", getSimpleName(), getId());
	}

	protected void error(Exception e) {
		log.error("Class: {}; ThreadId: {}; error!", getSimpleName(), getId(), e);
	}

	public void awaitTerminated() {
		ValueUtils.await(this::getState, State.TERMINATED::equals);
	}

}
