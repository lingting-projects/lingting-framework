package live.lingting.framework.http.okhttp;

import live.lingting.framework.http.HttpRequest;
import live.lingting.framework.http.body.BodySource;
import live.lingting.framework.http.body.FileBody;
import live.lingting.framework.stream.CloneInputStream;
import live.lingting.framework.util.StreamUtils;
import live.lingting.framework.util.StringUtils;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author lingting 2024-09-02 16:20
 */
@RequiredArgsConstructor
public class OkHttpRequestBody extends RequestBody {

	public static final MediaType MEDIA_STREAM = MediaType.parse("application/octet-stream");

	protected final BodySource source;

	protected final MediaType mediaType;

	public OkHttpRequestBody(File file) throws IOException {
		this(new CloneInputStream(file), MEDIA_STREAM);
	}

	public OkHttpRequestBody(InputStream stream) throws IOException {
		this(stream, MEDIA_STREAM);
	}

	public OkHttpRequestBody(InputStream input, String contentType) throws IOException {
		this(input, StringUtils.hasText(contentType) ? MediaType.parse(contentType) : null);
	}

	public OkHttpRequestBody(InputStream input, MediaType mediaType) throws IOException {
		CloneInputStream stream = input instanceof CloneInputStream clone ? clone : new CloneInputStream(input);
		this.source = new FileBody(stream);
		this.mediaType = mediaType;
	}

	public OkHttpRequestBody(HttpRequest.Body body) throws IOException {
		this(body.input(), body.contentType());
	}

	@Nullable
	@Override
	public MediaType contentType() {
		return mediaType;
	}

	@Override
	public long contentLength() {
		return source.length();
	}

	@Override
	public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
		StreamUtils.read(source.openInput(), (buffer, len) -> bufferedSink.write(buffer, 0, len));
	}

}
