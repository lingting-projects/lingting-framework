package live.lingting.framework.http.body;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author lingting 2024-09-28 14:04
 */
public abstract class BodySource {

	public static MemoryBody empty() {
		return new MemoryBody(new byte[0]);
	}

	public abstract long length();

	public abstract byte[] bytes();

	public abstract InputStream openInput();

	public String string() {
		return string(StandardCharsets.UTF_8);
	}

	public abstract String string(Charset charset);

	public abstract long transferTo(OutputStream output) throws IOException;

	public abstract long transferTo(WritableByteChannel channel) throws IOException;

}
