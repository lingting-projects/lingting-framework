package live.lingting.framework.util;

import lombok.experimental.UtilityClass;

/**
 * @author lingting
 */
@UtilityClass
public class CharUtils {

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
