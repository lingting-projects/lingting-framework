package live.lingting.framework.stream;

import live.lingting.framework.util.FileUtils;
import live.lingting.framework.util.StreamUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
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
@SuppressWarnings("java:S1170")
public class CloneInputStream extends InputStream {

	public static final File TEMP_DIR = FileUtils.createTempDir("clone");

	protected final Object lock = "";

	protected final File file;

	/**
	 * 字节数
	 */
	protected final long size;

	protected FileInputStream stream;

	@Getter
	@Setter
	protected boolean closeAndDelete = false;

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
	}

	public CloneInputStream(File file) {
		this.file = file;
		this.size = file.length();
	}

	protected FileInputStream getStream() throws IOException {
		if (stream != null) {
			return stream;
		}

		synchronized (lock) {
			if (stream != null) {
				return stream;
			}
			stream = new FileInputStream(file);
		}
		return stream;
	}

	@Override
	public int read(byte[] b) throws IOException {
		return getStream().read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return getStream().read(b, off, len);
	}

	@Override
	public long skip(long n) throws IOException {
		return getStream().skip(n);
	}

	@Override
	public int available() throws IOException {
		return getStream().available();
	}

	@Override
	public void close() {
		StreamUtils.close(stream);
		if (isCloseAndDelete()) {
			clear();
		}
	}

	@Override
	public void mark(int limit) {
		if (stream != null) {
			stream.mark(limit);
		}
	}

	@Override
	public void reset() throws IOException {
		if (stream != null) {
			stream.reset();
		}
	}

	@SneakyThrows
	@Override
	public boolean markSupported() {
		return getStream().markSupported();
	}

	@Override
	public int read() throws IOException {
		return getStream().read();
	}

	public long size() {
		return size;
	}

	public File file() {
		return file;
	}

	@SneakyThrows
	public CloneInputStream copy() {
		return new CloneInputStream(this);
	}

	public void clear() {
		FileUtils.delete(file);
	}

}
