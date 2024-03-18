
package live.lingting.polaris.grpc.util;

/**
 * @author lixiaoshuang
 */
public class JvmHookHelper {

	/**
	 * Add JVM callback hooks.
	 * @param runnable Functional interface
	 */
	public static boolean addShutdownHook(Runnable runnable) {
		Runtime.getRuntime().addShutdownHook(new Thread(runnable));
		return true;
	}

}
