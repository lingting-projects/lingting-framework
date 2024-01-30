package live.lingting.framework.util;

import lombok.experimental.UtilityClass;

import java.io.CharArrayWriter;
import java.util.Base64;
import java.util.Iterator;

/**
 * @author lingting
 */
@UtilityClass
public class StringUtils {

	public static final String BOM_UTF8 = "\uFEFF";

	public static final String BOM_UTF16B = "\uFEFF";

	public static final String BOM_UTF16S = "\uFFFE";

	public static final String BOM_UTF32B = "\u0000FEFF";

	public static final String BOM_UTF32S = "\uFFFFE0000";

	/**
	 * 指定字符串是否存在可见字符
	 * @param str 字符串
	 * @return boolean
	 */
	public static boolean hasText(CharSequence str) {
		if (str == null || str.isEmpty()) {
			return false;
		}

		for (int i = 0; i < str.length(); i++) {
			// 如果是非空白字符
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}

		return false;
	}

	public static String join(Iterable<?> iterable, String delimiter) {
		if (iterable == null) {
			return null;
		}

		return join(iterable.iterator(), delimiter);
	}

	public static String join(Iterator<?> iterator, String delimiter) {
		if (iterator == null) {
			return null;
		}

		StringBuilder builder = new StringBuilder();
		while (iterator.hasNext()) {
			final Object next = iterator.next();
			if (next == null) {
				continue;
			}
			builder.append(next);

			if (iterator.hasNext()) {
				builder.append(delimiter);
			}
		}

		return builder.toString();

	}

	public static String firstLower(String str) {
		if (!hasText(str)) {
			return str;
		}

		final char c = str.charAt(0);
		if (CharUtils.isUpperLetter(c)) {
			return Character.toLowerCase(c) + str.substring(1);
		}
		return str;
	}

	public static String firstUpper(String str) {
		if (!hasText(str)) {
			return str;
		}

		final char c = str.charAt(0);
		if (CharUtils.isLowerLetter(c)) {
			return Character.toUpperCase(c) + str.substring(1);
		}
		return str;
	}

	/**
	 * 驼峰字符串转下划线字符串
	 * <p>
	 * eg: HumpToUnderscore -> hump_to_underscore
	 * </p>
	 */
	public static String humpToUnderscore(String str) {
		CharArrayWriter writer = new CharArrayWriter();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			// 大写字母处理
			if (CharUtils.isUpperLetter(c)) {
				// 如果不是第一个大写字母, 插入下划线 _
				if (writer.size() > 0) {
					writer.append('_');
				}
				// 转小写
				writer.append(Character.toLowerCase(c));
			}
			// 不是则直接写入
			else {
				writer.append(c);
			}
		}

		return writer.toString();
	}

	/**
	 * 下划线字符串转驼峰字符串
	 * <p>
	 * eg: HumpToUnderscore -> hump_to_underscore
	 * </p>
	 */
	public static String underscoreToHump(String str) {
		CharArrayWriter writer = new CharArrayWriter();
		boolean upper = false;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			// 如果是下划线, 下一个要转大写
			if (c == '_') {
				upper = true;
				continue;
			}
			// 要转大写
			if (upper) {
				writer.append(Character.toUpperCase(c));
				upper = false;
			}
			// 保持原样
			else {
				writer.append(c);
			}
		}

		return writer.toString();
	}

	public static String hex(byte[] bytes) {
		StringBuilder builder = new StringBuilder();

		for (byte b : bytes) {
			String hex = Integer.toHexString((b & 0xFF) | 0x100);
			builder.append(hex, 1, 3);
		}

		return builder.toString();
	}

	public static byte[] hex(String hex) {
		int length = hex.length();
		if (length % 2 != 0) {
			throw new IllegalArgumentException("Invalid hexadecimal string");
		}

		byte[] byteArray = new byte[length / 2];
		for (int i = 0; i < length; i += 2) {
			// 16进制单字符
			String h = hex.substring(i, i + 2);
			// 转byte
			byte b = (byte) Integer.parseInt(h, 16);
			byteArray[i / 2] = b;
		}

		return byteArray;
	}

	/**
	 * 字节码转base64字符串
	 */
	public static String base64(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}

	/**
	 * base64字符串转字节码
	 */
	public static byte[] base64(String base64) {
		return Base64.getDecoder().decode(base64);
	}

	/**
	 * 往前缀追加 指定数量的指定字符
	 * @param prefix 前缀
	 * @param count 数量
	 * @param str 指定字符
	 * @return 追加完成后的字符串
	 */
	public static String append(String prefix, int count, String str) {
		return prefix + str.repeat(Math.max(0, count));
	}

	public static String cleanBom(String string) {
		return string.replace(BOM_UTF32S, "")
			.replace(BOM_UTF32B, "")
			.replace(BOM_UTF16S, "")
			.replace(BOM_UTF16B, "")
			.replace(BOM_UTF8, "");
	}

}
