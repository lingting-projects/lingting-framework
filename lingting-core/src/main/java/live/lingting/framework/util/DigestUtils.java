package live.lingting.framework.util;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
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

	public static byte[] md5(InputStream input) throws NoSuchAlgorithmException, IOException {
		return md5(input, StreamUtils.getReadSize());
	}

	public static byte[] md5(InputStream input, int size) throws NoSuchAlgorithmException, IOException {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		StreamUtils.read(input, size, (buffer, len) -> digest.update(buffer, 0, len));
		return digest.digest();
	}

	public static String md5Hex(String input) throws NoSuchAlgorithmException {
		return md5Hex(input.getBytes(StandardCharsets.UTF_8));
	}

	public static String md5Hex(byte[] input) throws NoSuchAlgorithmException {
		byte[] bytes = md5(input);
		return StringUtils.hex(bytes);
	}

	public static String md5Hex(InputStream input) throws NoSuchAlgorithmException, IOException {
		return md5Hex(input, StreamUtils.getReadSize());
	}

	public static String md5Hex(InputStream input, int size) throws NoSuchAlgorithmException, IOException {
		byte[] bytes = md5(input, size);
		return StringUtils.hex(bytes);
	}

}
