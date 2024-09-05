package live.lingting.framework.http.okhttp;

import live.lingting.framework.stream.CloneInputStream;
import live.lingting.framework.util.StreamUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author lingting 2024-09-02 16:20
 */
@RequiredArgsConstructor
public class OkHttpInputStreamRequestBody extends RequestBody {

	public static final MediaType MEDIA_STREAM = MediaType.parse("application/octet-stream");

	@Getter
	@Setter
	static int readSize = StreamUtils.getReadSize();

	protected final InputStream input;

	protected final long size;

	public OkHttpInputStreamRequestBody(InputStream stream) throws IOException {
		CloneInputStream clone = new CloneInputStream(stream);
		this.input = clone;
		this.size = clone.size();
	}

	public OkHttpInputStreamRequestBody(File file) throws FileNotFoundException {
		this.input = new FileInputStream(file);
		this.size = file.length();
	}

	@Nullable
	@Override
	public MediaType contentType() {
		return MEDIA_STREAM;
	}

	@Override
	public long contentLength() {
		return size;
	}

	@Override
	public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
		StreamUtils.read(input, readSize, (buffer, len) -> bufferedSink.write(buffer, 0, len));
	}

}
