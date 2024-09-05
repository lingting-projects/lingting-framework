package live.lingting.framework.stream;

import live.lingting.framework.util.FileUtils;
import lombok.Getter;

import java.io.File;
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

	static {
		FileUtils.createDir(TEMP_DIR);
	}

	protected final RandomAccessFile file;

	@Getter
	private final Path path;

	/**
	 * 文件大小: bytes
	 */
	@Getter
	private final long size;

	public RandomAccessInputStream(InputStream in) throws IOException {
		if (in instanceof RandomAccessInputStream stream) {
			this.file = new RandomAccessFile(stream.path.toFile(), MODE);
			this.path = stream.path;
			this.size = stream.size;
		}
		else {
			File temp = FileUtils.createTemp(in, ".input", TEMP_DIR);
			this.file = new RandomAccessFile(temp, MODE);
			this.path = temp.toPath();
			this.size = Files.size(path);
		}
	}

	public RandomAccessInputStream(String path) throws IOException {
		this(new File(path));
	}

	public RandomAccessInputStream(File file) throws IOException {
		this(file.toPath());
	}

	public RandomAccessInputStream(Path path) throws IOException {
		this(Files.newInputStream(path));
	}

	public void seek(long pos) throws IOException {
		file.seek(pos);
	}

	@Override
	public void close() throws IOException {
		file.close();
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
	public synchronized void reset() throws IOException {
		file.seek(0);
	}

}
