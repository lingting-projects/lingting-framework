package live.lingting.framework.okhttp;

import live.lingting.framework.exception.HttpException;
import live.lingting.framework.function.ThrowingFunction;
import live.lingting.framework.jackson.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author lingting 2023/1/31 13:59
 */
@Slf4j
public record OkHttp(OkHttpClient client) {

	public static OkHttpBuilder builder() {
		return new OkHttpBuilder();
	}

	public Response request(Request request) {
		Call call = client.newCall(request);
		try {
			return call.execute();
		}
		catch (Exception e) {
			throw new HttpException(e);
		}
	}

	// region 请求用
	public <T> T request(Request request, ThrowingFunction<Response, T> function) {
		try (Response response = request(request)) {
			return function.apply(response);
		}
		catch (HttpException e) {
			throw e;
		}
		catch (Exception e) {
			throw new HttpException("http response handler error!", e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T request(Request request, Class<T> cls) {
		return request(request, response -> {
			ResponseBody responseBody = response.body();
			if (responseBody == null) {
				return null;
			}

			String string = responseBody.string();
			if (cls.isAssignableFrom(String.class)) {
				return (T) string;
			}
			return JacksonUtils.toObj(string, cls);
		});
	}

	public Response get(String url) {
		Request.Builder builder = new Request.Builder().url(url).get();
		return request(builder.build());
	}

	public <T> T get(String url, Class<T> cls) {
		Request.Builder builder = new Request.Builder().url(url).get();
		return request(builder.build(), cls);
	}

	public Response get(HttpUrl url) {
		Request.Builder builder = new Request.Builder().url(url).get();
		return request(builder.build());
	}

	public <T> T get(HttpUrl url, Class<T> cls) {
		Request.Builder builder = new Request.Builder().url(url).get();
		return request(builder.build(), cls);
	}

	public Response post(String url, RequestBody body) {
		Request.Builder builder = new Request.Builder().url(url).post(body);
		return request(builder.build());
	}

	public <T> T post(String url, RequestBody requestBody, Class<T> cls) {
		Request.Builder builder = new Request.Builder().url(url).post(requestBody);
		return request(builder.build(), cls);
	}

	// endregion

	public CookieJar cookieJar() {
		return client.cookieJar();
	}

	public OkHttpBuilder newBuilder() {
		return builder().okHttpClientBuilder(client.newBuilder());
	}

	public OkHttp copy() {
		return newBuilder().build();
	}
	// endregion

}
