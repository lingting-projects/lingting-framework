package live.lingting.framework.context;

import lombok.Getter;
import lombok.experimental.UtilityClass;

/**
 * @author lingting 2023-12-06 17:13
 */
@UtilityClass
public class ContextHolder {

	@Getter
	private static boolean stop = true;

	public static void start() {
		stop = false;
	}

	public static void stop() {
		stop = true;
	}

}
