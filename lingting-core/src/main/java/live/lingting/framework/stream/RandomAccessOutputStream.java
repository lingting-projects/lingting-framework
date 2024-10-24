package live.lingting.framework.stream;

import live.lingting.framework.util.FileUtils;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author lingting 2024-01-16 20:29
 */
public class RandomAccessOutputStream extends OutputStream {

	public static final String MODE = "rw";

	public static final File TEMP_DIR = FileUtils.createTempDir("random");

	protected final RandomAccessFile file;

	@Getter
	protected final Path path;

	public RandomAccessOutputStream() throws IOException {
		this(FileUtils.createTemp(".output", TEMP_DIR));
	}

	public RandomAccessOutputStream(String path) throws IOException {
		this(new File(path));
	}

	public RandomAccessOutputStream(Path path) throws IOException {
		this(path.toFile());
	}

	public RandomAccessOutputStream(File file) throws IOException {
		this.file = new RandomAccessFile(file, MODE);
		this.path = file.toPath();
	}

	public void seek(long pos) throws IOException {
		file.seek(pos);
	}

	public long size() throws IOException {
		return Files.size(path);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		file.write(b, off, len);
	}

	@Override
	public void close() throws IOException {
		file.close();
	}

	@Override
	public void write(int b) throws IOException {
		file.write(b);
	}

}
