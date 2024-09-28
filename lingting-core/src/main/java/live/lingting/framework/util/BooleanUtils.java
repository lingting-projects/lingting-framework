package live.lingting.framework.util;

import lombok.experimental.UtilityClass;

/**
 * @author lingting 2023-05-06 14:16
 */
@UtilityClass
public class BooleanUtils {

	private static final String[] STR_TRUE = { "1", "true", "yes", "ok", "y", "t" };

	private static final String[] STR_FALSE = { "0", "false", "no", "n", "f" };

	public static boolean isTrue(Object obj) {
		if (obj instanceof String) {
			String string = (String) obj;
			return ArrayUtils.containsIgnoreCase(STR_TRUE, string);
		}
		if (obj instanceof Number) {
			Number number = (Number) obj;
			return number.doubleValue() > 0;
		}
		if (obj instanceof Boolean) {
			Boolean b = (Boolean) obj;
			return Boolean.TRUE.equals(b);
		}
		return false;
	}

	public static boolean isFalse(Object obj) {
		if (obj instanceof String) {
			String string = (String) obj;
			return ArrayUtils.containsIgnoreCase(STR_FALSE, string);
		}
		if (obj instanceof Number) {
			Number number = (Number) obj;
			return number.doubleValue() <= 0;
		}
		if (obj instanceof Boolean) {
			Boolean b = (Boolean) obj;
			return Boolean.FALSE.equals(b);
		}
		return false;
	}

}
