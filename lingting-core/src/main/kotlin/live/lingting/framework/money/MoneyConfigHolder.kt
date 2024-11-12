package live.lingting.framework.money;

import live.lingting.framework.thread.StackThreadLocal;

/**
 * @author lingting 2023-05-07 18:00
 */
public final class MoneyConfigHolder {

	private static final StackThreadLocal<MoneyConfig> THREAD_LOCAL = new StackThreadLocal<>();

	private MoneyConfigHolder() {throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");}

	public static MoneyConfig get() {
		return THREAD_LOCAL.get();
	}

	public static void put(MoneyConfig config) {
		THREAD_LOCAL.put(config);
	}

	public static void pop() {
		THREAD_LOCAL.pop();
	}

}
