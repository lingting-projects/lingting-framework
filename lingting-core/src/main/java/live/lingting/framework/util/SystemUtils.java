package live.lingting.framework.util;

import live.lingting.framework.value.LazyValue;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.nio.charset.Charset;

/**
 * @author lingting 2022/6/25 12:10
 */
@UtilityClass
public class SystemUtils {

	/**
	 * 当前系统是否为Windows系统, 参考以下系统API
	 * @see sun.awt.OSInfo#getOSType()
	 * @return boolean
	 */
	public static boolean isWindows() {
		return osName().contains("Windows");
	}

	public static boolean isLinux() {
		return osName().contains("Linux");
	}

	public static boolean isMacX() {
		return osName().contains("OS X");
	}

	public static boolean isMac() {
		return osName().contains("Mac OS");
	}

	public static boolean isAix() {
		return osName().contains("AIX");
	}

	public static String osName() {
		return System.getProperty("os.name");
	}

	static final LazyValue<Charset> charset = new LazyValue<>(() -> {
		try {
			String name = System.getProperty("sun.jnu.encoding");
			return Charset.forName(name);
		}
		catch (Exception e) {
			return Charset.defaultCharset();
		}
	});

	/**
	 * 获取系统字符集
	 */
	public static Charset charset() {
		return charset.get();
	}

	public static String lineSeparator() {
		return System.lineSeparator();
	}

	public static String fileSeparator() {
		return File.separator;
	}

	public static File tmpDir() {
		return new File(System.getProperty("java.io.tmpdir"));
	}

	public static File tmpDirLingting() {
		return new File(System.getProperty("java.io.tmpdir"), "lingting");
	}

	public static File homeDir() {
		return new File(System.getProperty("user.home"));
	}

	public static File homeDirLingting() {
		return new File(System.getProperty("user.home"), ".lingting");
	}

	public static File workDir() {
		return new File(System.getProperty("user.dir"));
	}

	public static String username() {
		return System.getProperty("user.name");
	}

	public static String javaVersion() {
		return System.getProperty("java.version");
	}

	public static void proxy(String host, Integer port) {
		proxyHttp(host, port);
		proxyHttps(host, port);
	}

	public static void proxyHttp(String host, Integer port) {
		System.setProperty("http.proxyHost", host);
		System.setProperty("http.proxyPort", port.toString());
	}

	public static void proxyHttps(String host, Integer port) {
		System.setProperty("https.proxyHost", host);
		System.setProperty("https.proxyPort", port.toString());
	}

}
