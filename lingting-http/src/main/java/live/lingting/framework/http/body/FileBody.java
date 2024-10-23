package live.lingting.framework.http.body;

import live.lingting.framework.stream.CloneInputStream;
import live.lingting.framework.util.StreamUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

/**
 * @author lingting 2024-09-28 14:04
 */
@RequiredArgsConstructor
public class FileBody extends BodySource {

	private final CloneInputStream input;

	@SneakyThrows
	public static BodySource of(InputStream inputStream) {
		if (inputStream instanceof CloneInputStream clone) {
			return new FileBody(clone);
		}
		return new FileBody(new CloneInputStream(inputStream));
	}

	@Override
	public long length() {
		return input.size();
	}

	@SneakyThrows
	@Override
	public byte[] bytes() {
		return input.copy().readAllBytes();
	}

	@Override
	public InputStream openInput() {
		return input.copy();
	}

	@SneakyThrows
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
