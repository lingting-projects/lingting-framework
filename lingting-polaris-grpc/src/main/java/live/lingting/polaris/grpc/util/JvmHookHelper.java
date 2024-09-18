
package live.lingting.polaris.grpc.util;

import lombok.experimental.UtilityClass;

/**
 * @author lixiaoshuang
 */
@UtilityClass
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
