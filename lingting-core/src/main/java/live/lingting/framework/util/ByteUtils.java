package live.lingting.framework.util;

import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * @author lingting 2023-12-22 11:28
 */
@UtilityClass
public class ByteUtils {

	public static byte[] toArray(List<Byte> list) {
		byte[] bytes = new byte[list.size()];
		for (int i = 0; i < list.size(); i++) {
			bytes[i] = list.get(i);
		}
		return bytes;
	}

	/**
	 * 两个字节是否表示行尾
	 * <p>
	 * 字节1在字节2前面
	 * </p>
	 * @param byte1 字节1
	 * @param byte2 字节2
	 * @return true 行尾
	 */
	public static boolean isEndLine(byte byte1, byte byte2) {
		return byte1 == '\r' && byte2 == '\n';
	}

	/**
	 * 字节是否表示行尾
	 * @param byte1 字节1
	 * @return true 行尾
	 */
	public static boolean isEndLine(byte byte1) {
		return byte1 == '\n';
	}

	/**
	 * 此数据是否为完整的一行数据(以换行符结尾)
	 * @param bytes 字节
	 * @return true 一整行
	 */
	public static boolean isLine(List<Byte> bytes) {
		int size = bytes.size();
		if (size < 1) {
			return false;
		}

		byte last = bytes.get(size - 1);
		if (isEndLine(last)) {
			return true;
		}
		if (size > 1) {
			byte penultimate = bytes.get(size - 2);
			return isEndLine(penultimate, last);
		}
		return false;
	}

	/**
	 * 此数据是否为完整的一行数据(以换行符结尾)
	 * @param bytes 字节
	 * @return true 一整行
	 */
	public static boolean isLine(byte[] bytes) {
		int size = bytes.length;
		if (size < 1) {
			return false;
		}

		byte last = bytes[size - 1];
		if (isEndLine(last)) {
			return true;
		}
		if (size > 1) {
			byte penultimate = bytes[size - 2];
			return isEndLine(penultimate, last);
		}
		return false;
	}

	/**
	 * 移除行数据中的行尾符合
	 * @param list 数据
	 * @return 移除后的数据, 如果没有则是原数据
	 */
	public static byte[] trimEndLine(List<Byte> list) {
		if (CollectionUtils.isEmpty(list)) {
			return new byte[0];
		}

		int lastIndex = list.size() - 1;
		byte last = list.get(lastIndex);

		// 大于2字节
		if (list.size() > 1) {
			int penultimateIndex = list.size() - 2;
			byte penultimate = list.get(penultimateIndex);

			// 如果是 2字节表示行尾
			if (isEndLine(penultimate, last)) {
				return toArray(list.subList(0, penultimateIndex));
			}
		}

		// 1字节表示行尾
		if (isEndLine(last)) {
			return toArray(list.subList(0, lastIndex));
		}

		// 如果没有行尾符
		return toArray(list);
	}

}
