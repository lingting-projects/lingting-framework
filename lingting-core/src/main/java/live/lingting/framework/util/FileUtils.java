package live.lingting.framework.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lingting
 */
@Slf4j
@UtilityClass
public class FileUtils {

	private static final File TEMP_DIR = SystemUtils.tmpDirLingting();

	/**
	 * 扫描指定路径下所有文件
	 * @param path 指定路径
	 * @param recursive 是否递归
	 * @return java.util.List<java.lang.String>
	 */
	public static List<String> scanFile(String path, boolean recursive) {
		List<String> list = new ArrayList<>();
		File file = new File(path);
		if (!file.exists()) {
			return list;
		}

		if (file.isFile()) {
			list.add(file.getAbsolutePath());
			return list;
		}

		// 文件夹
		File[] files = file.listFiles();
		if (ArrayUtils.isEmpty(files)) {
			return list;
		}

		for (File childFile : files) {
			// 如果递归
			if (recursive && childFile.isDirectory()) {
				list.addAll(scanFile(childFile.getAbsolutePath(), true));
			}
			// 是文件
			else if (childFile.isFile()) {
				list.add(childFile.getAbsolutePath());
			}
		}

		return list;
	}

	/**
	 * 创建指定文件夹, 已存在时不会重新创建
	 * @param dir 文件夹.
	 */
	public static boolean createDir(File dir) {
		if (dir.exists()) {
			return true;
		}
		return dir.mkdirs();
	}

	/**
	 * 创建指定文件, 已存在时不会重新创建
	 * @param file 文件.
	 */
	public static boolean createFile(File file) {
		if (file.exists()) {
			return true;
		}

		if (!createDir(file.getParentFile())) {
			return false;
		}

		try {
			return file.createNewFile();
		}
		catch (IOException e) {
			log.debug("file create error! path: {}", file.getAbsolutePath());
			return false;
		}
	}

	public static File createFile(String filename, File dir) throws IOException {
		if (!createDir(dir)) {
			throw new IOException("dir create error! path : " + dir.getAbsolutePath());
		}
		File file = new File(dir, filename);
		if (createFile(file)) {
			return file;
		}
		throw new IOException("file create error! path : " + file.getAbsolutePath());
	}

	/**
	 * 创建临时文件
	 */
	public static File createTemp() throws IOException {
		return createTemp(".tmp");
	}

	/**
	 * 创建临时文件
	 * @param suffix 文件后缀
	 * @return 临时文件对象
	 */
	public static File createTemp(String suffix) throws IOException {
		return createTemp(suffix, TEMP_DIR);
	}

	/**
	 * 创建临时文件
	 * @param suffix 文件特征
	 * @param dir 文件存放位置
	 * @return 临时文件对象
	 */
	public static File createTemp(String suffix, File dir) throws IOException {
		if (!createDir(dir)) {
			throw new IOException("temp dir create error! path : " + dir.getAbsolutePath());
		}
		return File.createTempFile("lingting.", suffix, dir);
	}

	public static File createTemp(InputStream in) throws IOException {
		File file = createTemp();

		try (FileOutputStream out = new FileOutputStream(file)) {
			StreamUtils.write(in, out);
		}

		return file;
	}

	/**
	 * 复制文件
	 * @param source 源文件
	 * @param target 目标文件
	 * @param override 如果目标文件已存在是否覆盖
	 * @param options 其他文件复制选项 {@link StandardCopyOption}
	 * @return 目标文件地址
	 */
	public static Path copy(File source, File target, boolean override, CopyOption... options) throws IOException {
		List<CopyOption> list = new ArrayList<>();
		if (override) {
			list.add(StandardCopyOption.REPLACE_EXISTING);
		}

		if (options != null && options.length > 0) {
			list.addAll(Arrays.asList(options));
		}

		return Files.copy(source.toPath(), target.toPath(), list.toArray(new CopyOption[0]));
	}

	public static void write(File file, InputStream in) throws IOException {
		if (!createFile(file)) {
			throw new FileNotFoundException("path: " + file.getAbsolutePath());
		}
		try (OutputStream out = Files.newOutputStream(file.toPath())) {
			StreamUtils.write(in, out);
		}
	}

	public static boolean delete(File file) {
		try {
			Files.delete(file.toPath());
			return true;
		}
		catch (IOException e) {
			return false;
		}
	}

	/**
	 * 依据系统文件路径分隔符解析
	 */
	public static String getFilename(String string) {
		Path path = Paths.get(string);
		return path.getFileName().toString();
	}

	public static String getFilenameByUrl(String url) {
		return getFilename(url, "/");
	}

	/**
	 * 依据指定分隔符解析
	 */
	public static String getFilename(String path, String delimiter) {
		if (!StringUtils.hasText(path)) {
			return "";
		}
		String[] split = path.split(delimiter);
		return split[split.length - 1];
	}

	/**
	 * 获取文件扩展名
	 * @return java.lang.String eg: java
	 */
	public static String getFileExt(String filename) {
		return getFileExt(filename, "\\.");
	}

	public static String getFileExt(String filename, String delimiter) {
		if (!StringUtils.hasText(filename)) {
			return "";
		}
		String[] split = filename.split(delimiter);
		return split[split.length - 1];
	}

}
