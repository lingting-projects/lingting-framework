package live.lingting.framework.context;

/**
 * @author lingting 2023-12-06 17:13
 */
public final class ContextHolder {

	private static boolean stop = true;

	private ContextHolder() {throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");}

	public static void start() {
		stop = false;
	}

	public static void stop() {
		stop = true;
	}

	public static boolean isStop() {return ContextHolder.stop;}
}
