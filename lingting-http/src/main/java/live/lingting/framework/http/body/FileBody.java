package live.lingting.framework.http.body;

import live.lingting.framework.stream.CloneInputStream;
import live.lingting.framework.util.StreamUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author lingting 2024-09-28 14:04
 */
@RequiredArgsConstructor
public class FileBody extends BodySource {

	private final CloneInputStream input;

	@SneakyThrows
	public static BodySource of(InputStream inputStream) {
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
		return StreamUtils.toString(input);
	}

}
