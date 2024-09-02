package live.lingting.framework.http;

import live.lingting.framework.flow.FutureSubscriber;
import live.lingting.framework.function.ThrowingFunction;
import live.lingting.framework.http.okhttp.OkHttpCookie;
import live.lingting.framework.http.okhttp.OkHttpResponse;
import live.lingting.framework.http.okhttp.OkHttpResponseCallback;
import live.lingting.framework.jackson.JacksonUtils;
import live.lingting.framework.util.StreamUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/**
 * @author lingting 2024-09-02 15:36
 */
@RequiredArgsConstructor
public class OkHttpClient extends HttpClient {

	/**
	 * 一次读取10M
	 */
	public static final int DEFAULT_SIZE = 1024 * 1024 * 10;

	protected final okhttp3.OkHttpClient client;

	@Override
	public okhttp3.OkHttpClient client() {
		return client;
	}

	public static Request convert(HttpRequest request) throws IOException {
		Request.Builder builder = new Request.Builder();

		// 请求头
		HttpHeaders headers = request.headers();
		headers.map().forEach((k, vs) -> vs.forEach(v -> builder.addHeader(k, v)));

		// 请求地址
		builder.url(request.uri().toURL());

		// 请求体
		Optional<HttpRequest.BodyPublisher> optional = request.bodyPublisher();
		RequestBody body = optional.map(publisher -> {
			if (publisher.contentLength() < 1) {
				return null;
			}
			MediaType type = headers.firstValue("Content-Type").map(MediaType::parse).orElse(null);

			FutureSubscriber<RequestBody, ByteBuffer> subscriber = new FutureSubscriber<>() {

				@Override
				public RequestBody convert(List<ByteBuffer> list) {
					int size = list.stream().mapToInt(ByteBuffer::remaining).sum();
					byte[] bytes = new byte[size];

					int len = 0;
					for (ByteBuffer buffer : list) {
						int remaining = buffer.remaining();
						buffer.get(bytes, len, remaining);
						len += remaining;
					}

					return RequestBody.create(bytes, type);
				}
			};

			publisher.subscribe(subscriber);
			return subscriber.get();
		}).orElse(null);
		builder.method(request.method(), body);
		return builder.build();
	}

	@SneakyThrows
	public static <T> HttpResponse<T> convert(HttpRequest request, Response response,
			HttpResponse.BodyHandler<T> handler) {
		HttpResponse.ResponseInfo info = new OkHttpResponse.ResponseInfo(response);

		HttpResponse.BodySubscriber<T> subscriber = handler.apply(info);

		CompletableFuture<T> cf = new CompletableFuture<>();

		try {
			ResponseBody body = response.body();
			if (body != null) {
				List<ByteBuffer> buffers = new ArrayList<>();
				InputStream stream = body.byteStream();
				StreamUtils.read(stream, DEFAULT_SIZE, bytes -> {
					ByteBuffer buffer = ByteBuffer.wrap(bytes);
					buffers.add(buffer);
				});
				subscriber.onNext(buffers);
			}
			subscriber.onComplete();

			subscriber.getBody().whenComplete((r, t) -> {
				if (t != null) {
					cf.completeExceptionally(t);
				}
				else {
					cf.complete(r);
				}
			});

			T t = cf.get();
			return new OkHttpResponse<>(request, info, t);
		}
		catch (ExecutionException e) {
			throw e.getCause();
		}
	}

	@Override
	public <T> HttpResponse<T> request(HttpRequest request, HttpResponse.BodyHandler<T> handler) throws IOException {
		Request okhttp = convert(request);

		try (Response response = request(okhttp)) {
			return convert(request, response, handler);
		}
	}

	@Override
	public <T> void request(HttpRequest request, HttpResponse.BodyHandler<T> handler, ResponseCallback<T> callback)
			throws IOException {
		Request okhttp = convert(request);
		OkHttpResponseCallback<T> enqueue = new OkHttpResponseCallback<>(request, handler, callback);
		request(okhttp, enqueue);
	}

	// region 原始请求

	public Response request(Request request) throws IOException {
		Call call = client.newCall(request);
		return call.execute();
	}

	public void request(Request request, Callback callback) {
		Call call = client.newCall(request);
		call.enqueue(callback);
	}

	@SneakyThrows
	public <T> T request(Request request, ThrowingFunction<Response, T> function) throws IOException {
		try (Response response = request(request)) {
			return function.apply(response);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T request(Request request, Class<T> cls) throws IOException {
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

	public Response get(String url) throws IOException {
		Request.Builder builder = new Request.Builder().url(url).get();
		return request(builder.build());
	}

	public <T> T get(String url, Class<T> cls) throws IOException {
		Request.Builder builder = new Request.Builder().url(url).get();
		return request(builder.build(), cls);
	}

	public Response get(HttpUrl url) throws IOException {
		Request.Builder builder = new Request.Builder().url(url).get();
		return request(builder.build());
	}

	public <T> T get(HttpUrl url, Class<T> cls) throws IOException {
		Request.Builder builder = new Request.Builder().url(url).get();
		return request(builder.build(), cls);
	}

	public Response post(String url, RequestBody body) throws IOException {
		Request.Builder builder = new Request.Builder().url(url).post(body);
		return request(builder.build());
	}

	public <T> T post(String url, RequestBody requestBody, Class<T> cls) throws IOException {
		Request.Builder builder = new Request.Builder().url(url).post(requestBody);
		return request(builder.build(), cls);
	}

	// endregion

	public static class Builder extends HttpClient.Builder<OkHttpClient, OkHttpClient.Builder> {

		protected Authenticator authenticator;

		private Dispatcher dispatcher;

		public Builder authenticator(Authenticator authenticator) {
			this.authenticator = authenticator;
			return this;
		}

		public Builder dispatcher(Dispatcher dispatcher) {
			this.dispatcher = dispatcher;
			return this;
		}

		public OkHttpClient build(Supplier<okhttp3.OkHttpClient.Builder> supplier) {
			okhttp3.OkHttpClient.Builder builder = supplier.get();
			nonNull(socketFactory, builder::socketFactory);
			nonNull(hostnameVerifier, builder::hostnameVerifier);

			if (sslSocketFactory != null && trustManager != null) {
				builder.sslSocketFactory(sslSocketFactory, trustManager);
			}

			nonNull(callTimeout, builder::callTimeout);
			nonNull(connectTimeout, builder::connectTimeout);
			nonNull(readTimeout, builder::readTimeout);
			nonNull(writeTimeout, builder::writeTimeout);
			nonNull(proxySelector, builder::proxySelector);
			nonNull(authenticator, builder::authenticator);

			if (cookie != null) {
				builder.cookieJar(new OkHttpCookie(cookie));
			}

			if (dispatcher == null && executor != null) {
				dispatcher = new Dispatcher(executor);
			}

			nonNull(dispatcher, builder::dispatcher);

			okhttp3.OkHttpClient client = builder.build();
			return new OkHttpClient(client);
		}

		@Override
		protected OkHttpClient doBuild() {
			return build(okhttp3.OkHttpClient.Builder::new);
		}

	}

}
