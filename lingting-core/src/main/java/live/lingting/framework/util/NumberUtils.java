package live.lingting.framework.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 位运算
 *
 * @author lingting 2023-11-24 11:48
 */
@UtilityClass
public class NumberUtils {

	public static final BigDecimal DECIMAL_TWO = new BigDecimal("2");

	public static final BigInteger INTEGER_TWO = DECIMAL_TWO.toBigInteger();

	/**
	 * 指定数字是否为整数
	 */
	public static boolean isInteger(Number v) {
		if (v instanceof Double || v instanceof Float) {
			return false;
		}

		if (v instanceof BigDecimal decimal) {
			return decimal.scale() <= 0 || decimal.stripTrailingZeros().scale() <= 0;
		}

		return true;
	}

	/**
	 * 是否为大数
	 */
	public static boolean isBig(Number v) {
		return v instanceof BigDecimal || v instanceof BigInteger;
	}

	/**
	 * 指定数字是否为 2 的整数次幂
	 * <p>
	 * 对于大数, 仅支持正整数
	 * </p>
	 * @return true 表示是2的整数次幂
	 */
	public static boolean isPower2(Number v) {
		if (isBig(v)) {
			// 非整数大数为false
			if (!isInteger(v)) {
				return false;
			}

			BigInteger bi;
			if (v instanceof BigDecimal bd) {
				bi = bd.toBigInteger();
			}
			else {
				bi = (BigInteger) v;
			}

			/*
			 * 正数的符号位是0
			 *
			 * 正数的补码是原码
			 *
			 * 所以如果 补码形式与符号位不同的位数为1, 即 原码中与符号位不同的位数为1, 比 原码中与 0 不同的位数只有一个, 表示该数为2的整数次幂
			 */
			return bi.bitCount() == 1;

		}

		if (isInteger(v)) {
			long l = v.longValue();
			long b = l & (l - 1);
			return b == 0;
		}

		// 带小数的转decimal
		double d = v.doubleValue();
		String ds = Double.toString(d);
		BigDecimal decimal = new BigDecimal(ds);

		// 如果是整数则重新算, 否则为false
		if (isInteger(decimal)) {
			return isPower2(decimal.longValue());
		}
		return false;
	}

	/**
	 * 指定数字是否为偶数
	 */
	public static boolean isEven(Number v) {
		if (v instanceof BigDecimal bd) {
			BigDecimal[] decimals = bd.divideAndRemainder(DECIMAL_TWO);
			// 余数为0表示是偶数
			return decimals[1].compareTo(BigDecimal.ZERO) == 0;
		}
		if (v instanceof BigInteger bi) {
			BigInteger[] integers = bi.divideAndRemainder(INTEGER_TWO);
			// 余数为0表示是偶数
			return integers[1].compareTo(BigInteger.ZERO) == 0;
		}
		if (isPower2(v)) {
			return true;
		}
		if (isInteger(v)) {
			return v.longValue() % 2 == 0;
		}
		return v.doubleValue() % 2 == 0;
	}

	/**
	 * 计算数字的整数部分二进制位数
	 */
	public static int bitLength(Number v) {
		// 大数处理
		if (isBig(v)) {
			BigInteger bi;
			if (v instanceof BigDecimal bd) {
				bi = bd.toBigInteger();
			}
			else {
				bi = (BigInteger) v;
			}
			return bi.bitLength();
		}
		else {
			long l = v.longValue();
			String binaryString = Long.toBinaryString(l);
			return binaryString.length();
		}
	}

	/**
	 * 获取指定数字的下一个2的整数次幂值
	 */
	public static BigInteger nextPower2(Number v) {
		if (isPower2(v)) {
			if (v instanceof BigDecimal bd) {
				return bd.toBigInteger();
			}
			else if (v instanceof BigInteger bi) {
				return bi;
			}

			return new BigInteger(Long.toString(v.longValue()));
		}

		int length = bitLength(v);
		// 拼接下一个2的整数次幂值的二进制字符串
		String binary = StringUtils.append("1", length, "0");
		// 转数字
		return new BigInteger(binary, 2);
	}

	public static BigDecimal toNumber(Object val) {
		if (val == null) {
			return null;
		}
		String string = val.toString();
		if (!StringUtils.hasText(string)) {
			return null;
		}
		try {
			return new BigDecimal(string.trim());
		}
		catch (Exception e) {
			return null;
		}
	}

}
