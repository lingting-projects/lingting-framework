package live.lingting.framework.http.okhttp;

import live.lingting.framework.http.HttpRequest;
import live.lingting.framework.http.body.BodySource;
import live.lingting.framework.http.body.FileBody;
import live.lingting.framework.http.body.MemoryBody;
import live.lingting.framework.stream.FileCloneInputStream;
import live.lingting.framework.util.StreamUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static live.lingting.framework.http.okhttp.OkHttpUtils.mediaType;

/**
 * @author lingting 2024-09-02 16:20
 */
public class OkHttpRequestBody extends RequestBody {

	public static final MediaType MEDIA_STREAM = MediaType.parse("application/octet-stream");

	protected final BodySource source;

	protected final MediaType mediaType;

	public OkHttpRequestBody(File file) throws IOException {
		this(new FileCloneInputStream(file), MEDIA_STREAM);
	}

	public OkHttpRequestBody(InputStream stream) throws IOException {
		this(stream, MEDIA_STREAM);
	}

	public OkHttpRequestBody(InputStream input, String contentType) throws IOException {
		this(input, mediaType(contentType));
	}

	public OkHttpRequestBody(InputStream input, MediaType mediaType) throws IOException {
		this(new FileBody(input), mediaType);
	}

	public OkHttpRequestBody(BodySource source, String contentType) {
		this(source, mediaType(contentType));
	}

	public OkHttpRequestBody(BodySource source, MediaType mediaType) {
		this.source = source;
		this.mediaType = mediaType;
	}

	public OkHttpRequestBody(HttpRequest.Body body) {
		this(body.source(), body.contentType());
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
		if (source instanceof MemoryBody) {
			bufferedSink.write(source.bytes());
			return;
		}

		StreamUtils.read(source.openInput(), (buffer, len) -> bufferedSink.write(buffer, 0, len));
	}

}
