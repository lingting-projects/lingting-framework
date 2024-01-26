package live.lingting.framework.util;

import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author lingting 2023-10-08 14:41
 */
@UtilityClass
public class DigestUtils {

	public static byte[] md5(String input) throws NoSuchAlgorithmException {
		return md5(input.getBytes(StandardCharsets.UTF_8));
	}

	public static byte[] md5(byte[] input) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		return digest.digest(input);
	}

	public static String md5Hex(String input) throws NoSuchAlgorithmException {
		return md5Hex(input.getBytes(StandardCharsets.UTF_8));
	}

	public static String md5Hex(byte[] input) throws NoSuchAlgorithmException {
		byte[] bytes = md5(input);
		return StringUtils.hex(bytes);
	}

}
