package live.lingting.framework.stream;

import live.lingting.framework.util.FileUtils;
import live.lingting.framework.util.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 克隆输入流, 可直接读取, 也可以克隆出一个新流然后读取
 * <p>
 * 当直接读取时, 所有行为和文件流一致
 * </p>
 *
 * @author lingting 2024-01-09 15:41
 */
public class CloneInputStream extends InputStream {

	public static final File TEMP_DIR = FileUtils.createTempDir("clone");

	protected final File file;

	/**
	 * 字节数
	 */
	protected final long size;

	protected final FileInputStream stream;

	public CloneInputStream(InputStream input) throws IOException {
		if (input instanceof CloneInputStream clone) {
			this.file = clone.file;
			this.size = clone.size;
		}
		else {
			File temp = FileUtils.createTemp(input, ".clone", TEMP_DIR);
			this.file = temp;
			this.size = temp.length();
		}
		this.stream = new FileInputStream(file);
	}

	public CloneInputStream(File file) throws IOException {
		this.file = file;
		this.size = file.length();
		this.stream = new FileInputStream(file);
	}

	@Override
	public int read(byte[] b) throws IOException {
		return stream.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return stream.read(b, off, len);
	}

	@Override
	public long skip(long n) throws IOException {
		return stream.skip(n);
	}

	@Override
	public int available() throws IOException {
		return stream.available();
	}

	@Override
	public void close() {
		StreamUtils.close(stream);
	}

	@Override
	public synchronized void mark(int readlimit) {
		stream.mark(readlimit);
	}

	@Override
	public synchronized void reset() throws IOException {
		stream.reset();
	}

	@Override
	public boolean markSupported() {
		return stream.markSupported();
	}

	@Override
	public int read() throws IOException {
		return stream.read();
	}

	public long size() {
		return size;
	}

	public File file() {
		return file;
	}

	public FileInputStream copy() throws FileNotFoundException {
		return new FileInputStream(file);
	}

	public void clear() {
		FileUtils.delete(file);
	}

}
