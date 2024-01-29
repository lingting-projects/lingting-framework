package live.lingting.framework.sensitive;

import lombok.experimental.UtilityClass;

/**
 * @author lingting 2023-06-30 17:57
 */
@UtilityClass
public class SensitiveHolder {

	private static final ThreadLocal<Boolean> THREAD_LOCAL = new ThreadLocal<>();

	public static boolean allowSensitive() {
		return !Boolean.FALSE.equals(THREAD_LOCAL.get());
	}

	public static void setSensitive(boolean flag) {
		THREAD_LOCAL.set(flag);
	}

	public static void remove() {
		THREAD_LOCAL.remove();
	}

}
