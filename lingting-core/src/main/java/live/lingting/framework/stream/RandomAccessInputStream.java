package live.lingting.framework.stream;

import live.lingting.framework.util.FileUtils;
import live.lingting.framework.util.StreamUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author lingting 2024-09-05 14:38
 */
public class RandomAccessInputStream extends InputStream {

	public static final String MODE = "r";

	public static final File TEMP_DIR = RandomAccessOutputStream.TEMP_DIR;

	protected final RandomAccessFile file;

	@Getter
	private final Path path;

	/**
	 * 文件大小: bytes
	 */
	@Getter
	private final long size;

	@Getter
	@Setter
	protected boolean closeAndDelete = true;

	public RandomAccessInputStream(InputStream in) throws IOException {
		File temp = FileUtils.createTemp(".input", TEMP_DIR);

		if (in instanceof RandomAccessInputStream stream) {
			Files.copy(stream.path, temp.toPath());
		}
		else {
			StreamUtils.write(in, new FileOutputStream(temp));
		}

		this.file = new RandomAccessFile(temp, MODE);
		this.path = temp.toPath();
		this.size = temp.length();
	}

	public RandomAccessInputStream(String path) throws IOException {
		this(new File(path));
	}

	public RandomAccessInputStream(File file) throws IOException {
		this(file.toPath());
	}

	public RandomAccessInputStream(Path path) throws IOException {
		File temp = FileUtils.createTemp(".input", TEMP_DIR);
		Files.copy(path, temp.toPath());
		this.file = new RandomAccessFile(temp, MODE);
		this.path = temp.toPath();
		this.size = temp.length();
	}

	public void seek(long pos) throws IOException {
		file.seek(pos);
	}

	@Override
	public void close() throws IOException {
		file.close();
		if (closeAndDelete) {
			Files.deleteIfExists(path);
		}
	}

	@Override
	public int read() throws IOException {
		return file.read();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return file.read(b, off, len);
	}

	@Override
	public void reset() throws IOException {
		file.seek(0);
	}

}
