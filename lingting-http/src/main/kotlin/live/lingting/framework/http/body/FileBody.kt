package live.lingting.framework.http.body;

import live.lingting.framework.stream.FileCloneInputStream;
import live.lingting.framework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

/**
 * @author lingting 2024-09-28 14:04
 */
public class FileBody extends BodySource {

	private final FileCloneInputStream input;

	public FileBody(FileCloneInputStream input) {
		this.input = input;
	}

	public FileBody(InputStream input) throws IOException {
		this(new FileCloneInputStream(input));
	}

	@Override
	public long length() {
		return input.size();
	}


	@Override
	public byte[] bytes() {
		return input.copy().readAllBytes();
	}

	@Override
	public InputStream openInput() {
		return input.copy();
	}


	@Override
	public String string(Charset charset) {
		return StreamUtils.toString(input.copy(), charset);
	}

	@Override
	public long transferTo(OutputStream output) throws IOException {
		StreamUtils.write(input.copy(), output);
		return input.size();
	}

	@Override
	public long transferTo(WritableByteChannel channel) throws IOException {
		StreamUtils.read(input.copy(), (bytes, len) -> channel.write(ByteBuffer.wrap(bytes, 0, len)));
		return input.size();
	}

}
