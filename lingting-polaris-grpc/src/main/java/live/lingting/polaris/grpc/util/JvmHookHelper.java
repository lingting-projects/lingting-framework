
package live.lingting.polaris.grpc.util;

/**
 * @author lixiaoshuang
 */
public final class JvmHookHelper {

	private JvmHookHelper() {throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");}

	/**
	 * Add JVM callback hooks.
	 *
	 * @param runnable Functional interface
	 */
	public static boolean addShutdownHook(Runnable runnable) {
		Runtime.getRuntime().addShutdownHook(new Thread(runnable));
		return true;
	}

}
