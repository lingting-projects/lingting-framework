package live.lingting.framework.stream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * @author lingting 2024-01-16 20:29
 */
public class RandomAccessFileOutputStream extends OutputStream {

	protected final RandomAccessFile file;

	public RandomAccessFileOutputStream(String path) throws FileNotFoundException {
		this(new RandomAccessFile(path, "rw"));
	}

	public RandomAccessFileOutputStream(File file) throws FileNotFoundException {
		this(new RandomAccessFile(file, "rw"));
	}

	public RandomAccessFileOutputStream(RandomAccessFile file) {
		this.file = file;
	}

	public void seek(long pos) throws IOException {
		file.seek(pos);
	}

	@Override
	public void write(byte[] b) throws IOException {
		file.write(b);
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
