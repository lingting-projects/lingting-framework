package live.lingting.framework.http.body;

import live.lingting.framework.stream.BytesInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author lingting 2024-09-28 14:04
 */
public class MemoryBody extends BodySource {

	private final byte[] bytes;

	public MemoryBody(byte[] bytes) {
		this.bytes = bytes;
	}

	public MemoryBody(String string) {
		this(string, StandardCharsets.UTF_8);
	}

	public MemoryBody(String string, Charset charset) {
		this(string.getBytes(charset));
	}

	public MemoryBody(BytesInputStream stream) {
		this(stream.source());
	}

	public MemoryBody(InputStream stream) throws IOException {
		this(stream.readAllBytes());
	}

	@Override
	public long length() {
		return bytes.length;
	}

	@Override
	public byte[] bytes() {
		return bytes;
	}

	@Override
	public InputStream openInput() {
		return new ByteArrayInputStream(bytes);
	}

	@Override
	public String string(Charset charset) {
		return new String(bytes, charset);
	}

	@Override
	public long transferTo(OutputStream output) throws IOException {
		output.write(bytes);
		return bytes.length;
	}

	@Override
	public long transferTo(WritableByteChannel channel) throws IOException {
		channel.write(ByteBuffer.wrap(bytes));
		return bytes.length;
	}

}
