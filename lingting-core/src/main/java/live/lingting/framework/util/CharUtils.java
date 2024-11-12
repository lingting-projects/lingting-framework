package live.lingting.framework.util;

/**
 * @author lingting
 */
public final class CharUtils {

	private CharUtils() {throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");}

	public static boolean isLowerLetter(char c) {
		return c >= 'a' && c <= 'z';
	}

	public static boolean isUpperLetter(char c) {
		return c >= 'A' && c <= 'Z';
	}

	public static boolean isLetter(char c) {
		return isLowerLetter(c) || isUpperLetter(c);
	}

}
