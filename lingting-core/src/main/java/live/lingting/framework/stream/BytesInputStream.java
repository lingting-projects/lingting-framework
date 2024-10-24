package live.lingting.framework.stream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author lingting 2024/10/24 10:59
 */
public class BytesInputStream extends CloneInputStream {

	public BytesInputStream(File source) throws IOException {
		this(new FileInputStream(source));
	}

	public BytesInputStream(BytesInputStream input) {
		this(input.source());
	}

	public BytesInputStream(InputStream input) throws IOException {
		this(input.readAllBytes());
	}

	public BytesInputStream(byte[] source) {
		super(source, source.length);
	}

	@Override
	public byte[] readAllBytes() {
		return source();
	}

	@Override
	protected InputStream newStream() {
		return new ByteArrayInputStream(source());
	}

	@Override
	public BytesInputStream copy() {
		return new BytesInputStream(this);
	}

	@Override
	public byte[] source() {
		return (byte[]) source;
	}

	@Override
	public void clear() {
		//
	}

}
