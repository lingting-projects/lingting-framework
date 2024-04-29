package live.lingting.framework.thread;

import live.lingting.framework.util.MdcUtils;
import live.lingting.framework.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Map;

/**
 * 保留状态的可运行代码
 *
 * @author lingting 2024-04-28 17:25
 */
@Slf4j
@SuppressWarnings("java:S112")
public abstract class KeepRunnable implements Runnable {

	private final String name;

	private final Map<String, String> mdc;

	protected KeepRunnable() {
		this("");
	}

	protected KeepRunnable(String name) {
		this(name, MdcUtils.copyContext());
	}

	protected KeepRunnable(String name, Map<String, String> mdc) {
		this.name = name;
		this.mdc = mdc;
	}

	@Override
	public void run() {
		Thread thread = Thread.currentThread();
		String oldName = thread.getName();
		if (StringUtils.hasText(name)) {
			thread.setName(name);
		}

		Map<String, String> oldMdc = MdcUtils.copyContext();
		MDC.setContextMap(mdc);

		try {
			process();
		}
		catch (InterruptedException e) {
			thread.interrupt();
			log.warn("Thread interrupted inside thread pool");
		}
		catch (Throwable throwable) {
			log.error("Thread exception inside thread pool!", throwable);
		}
		finally {
			onFinally();
			MDC.setContextMap(oldMdc);
			thread.setName(oldName);
		}
	}

	protected abstract void process() throws Throwable;

	protected void onFinally() {

	}

}
