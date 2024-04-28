package live.lingting.framework.money;

import live.lingting.framework.thread.StackThreadLocal;
import lombok.experimental.UtilityClass;

/**
 * @author lingting 2023-05-07 18:00
 */
@UtilityClass
public class MoneyConfigHolder {

	private static final StackThreadLocal<MoneyConfig> THREAD_LOCAL = new StackThreadLocal<>();

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
