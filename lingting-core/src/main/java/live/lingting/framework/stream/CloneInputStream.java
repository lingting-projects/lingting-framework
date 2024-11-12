package live.lingting.framework.stream;

import live.lingting.framework.util.FileUtils;
import live.lingting.framework.util.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author lingting 2024/10/24 11:00
 */
@SuppressWarnings("java:S1170")
public abstract class CloneInputStream extends InputStream {

	public static final File TEMP_DIR = FileUtils.createTempDir("clone");

	protected final Object lock = "";

	protected final Object source;

	/**
	 * 字节数
	 */
	protected final long size;

	protected InputStream stream;

	protected boolean closeAndDelete = false;

	public CloneInputStream(Object source, long size) {
		this.source = source;
		this.size = size;
	}

	protected InputStream getStream() throws IOException {
		if (stream != null) {
			return stream;
		}

		synchronized (lock) {
			if (stream != null) {
				return stream;
			}
			stream = newStream();
		}
		return stream;
	}

	protected abstract InputStream newStream() throws IOException;

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

	public Object source() {
		return source;
	}

	public abstract CloneInputStream copy();

	public abstract void clear();

	public boolean isCloseAndDelete() {return this.closeAndDelete;}

	public void setCloseAndDelete(boolean closeAndDelete) {this.closeAndDelete = closeAndDelete;}
}
