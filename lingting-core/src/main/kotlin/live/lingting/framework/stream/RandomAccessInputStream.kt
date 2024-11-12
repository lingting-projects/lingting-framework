package live.lingting.framework.stream;

import live.lingting.framework.util.FileUtils;

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

	protected final RandomAccessFile file;

	private final Path path;

	/**
	 * 文件大小: bytes
	 */
	private final long size;

	protected boolean closeAndDelete = false;

	public RandomAccessInputStream(InputStream in) throws IOException {
		File temp;

		if (in instanceof RandomAccessInputStream stream) {
			this.closeAndDelete = false;
			temp = stream.path.toFile();
		}
		else {
			this.closeAndDelete = true;
			temp = FileUtils.createTemp(in, ".input", TEMP_DIR);
		}

		this.file = new RandomAccessFile(temp, MODE);
		this.path = temp.toPath();
		this.size = temp.length();
	}

	public RandomAccessInputStream(String path) throws IOException {
		this(new File(path));
	}

	public RandomAccessInputStream(File file) throws IOException {
		this.file = new RandomAccessFile(file, MODE);
		this.path = file.toPath();
		this.size = file.length();
	}

	public RandomAccessInputStream(Path path) throws IOException {
		this(path.toFile());
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

	public Path getPath() {return this.path;}

	public long getSize() {return this.size;}

	public boolean isCloseAndDelete() {return this.closeAndDelete;}

	public void setCloseAndDelete(boolean closeAndDelete) {this.closeAndDelete = closeAndDelete;}
}
