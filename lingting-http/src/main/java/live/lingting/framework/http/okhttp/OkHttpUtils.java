package live.lingting.framework.http.okhttp;

import live.lingting.framework.http.HttpRequest;
import live.lingting.framework.util.StringUtils;
import lombok.experimental.UtilityClass;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author lingting 2024-09-28 14:11
 */
@UtilityClass
public class OkHttpUtils {

	public static InputStream input(RequestBody body) throws IOException {
		if (body == null) {
			return null;
		}
		Buffer buffer = new Buffer();
		body.writeTo(buffer);
		return buffer.inputStream();
	}

	public static byte[] bytes(RequestBody body) {
		if (body == null) {
			return new byte[0];
		}
		Buffer buffer = new Buffer();
		try {
			body.writeTo(buffer);
		}
		catch (IOException e) {
			return new byte[0];
		}
		return buffer.readByteArray();
	}

	public static MediaType mediaType(HttpRequest.Body body) {
		return mediaType(body == null ? null : body.contentType());
	}

	public static MediaType mediaType(String contentType) {
		return StringUtils.hasText(contentType) ? MediaType.parse(contentType) : null;
	}

}
