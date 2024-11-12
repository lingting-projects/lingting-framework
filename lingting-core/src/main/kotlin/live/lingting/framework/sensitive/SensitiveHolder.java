package live.lingting.framework.sensitive;

/**
 * @author lingting 2023-06-30 17:57
 */
public final class SensitiveHolder {

	private static final ThreadLocal<Boolean> THREAD_LOCAL = new ThreadLocal<>();

	private SensitiveHolder() {throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");}

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
