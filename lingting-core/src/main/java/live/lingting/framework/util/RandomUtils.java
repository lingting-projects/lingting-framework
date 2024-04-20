package live.lingting.framework.util;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author lingting
 */
@UtilityClass
public class RandomUtils {

	/**
	 * 用于随机选的数字
	 */
	public static final String NUMBER = "0123456789";

	/**
	 * 用于随机选的字母
	 */
	public static final String LETTER = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	/**
	 * 用于生成16进制
	 */
	public static final String HEX = "0123456789ABCEDF";

	/**
	 * 用于随机选的字符
	 */
	public static final String STRING = NUMBER + LETTER;

	public static ThreadLocalRandom getRandom() {
		return ThreadLocalRandom.current();
	}

	/**
	 * 随机数
	 * @param max 最大值 - 不包括该值
	 * @return int
	 */
	public static int nextInt(int max) {
		return nextInt(0, max);
	}

	/**
	 * 随机数
	 * @param min 最小值 - 包括该值
	 * @param max 最大值 - 不包括该值
	 * @return int
	 */
	public static int nextInt(int min, int max) {
		return getRandom().nextInt(min, max);
	}

	/**
	 * 从指定字符串中随机生成字符串
	 * @param base 根字符
	 * @param len 长度
	 * @return java.lang.String
	 */
	public static String nextStr(String base, int len) {
		if (!StringUtils.hasText(base)) {
			base = STRING;
		}

		StringBuilder builder = new StringBuilder(len);

		for (int i = 0; i < len; i++) {
			int index = nextInt(base.length());
			char c = base.charAt(index);
			builder.append(c);
		}

		return builder.toString();
	}

	public static String nextStr(int len) {
		return nextStr(STRING, len);
	}

	public static String nextLetter(int len) {
		return nextStr(LETTER, len);
	}

	public static String nextNumber(int len) {
		return nextStr(NUMBER, len);
	}

	public static String nextHex(int len) {
		final String hex = nextStr(HEX, len);
		if (len > 1 && hex.charAt(0) == 0) {
			return hex.substring(1) + nextHex(1);
		}
		return hex;
	}

}
