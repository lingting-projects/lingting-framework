package live.lingting.framework.http.okhttp;

import live.lingting.framework.flow.FutureSubscriber;
import live.lingting.framework.http.HttpDelegateClient;
import live.lingting.framework.http.ResponseCallback;
import live.lingting.framework.util.StreamUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

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

/**
 * @author lingting 2024-05-07 13:52
 */
@RequiredArgsConstructor
public class OkHttpDelegateClient extends HttpDelegateClient<OkHttpClient> {

	/**
	 * 一次读取10M
	 */
	public static final int DEFAULT_SIZE = 1024 * 1024 * 10;

	private final OkHttpClient client;

	@Override
	public OkHttpClient client() {
		return client;
	}

	protected Call convert(HttpRequest request) throws IOException {
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
		return client.newCall(builder.build());
	}

	@SneakyThrows
	protected <T> HttpResponse<T> convert(HttpRequest request, Response response, HttpResponse.BodyHandler<T> handler) {
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
		Call call = convert(request);

		try (Response response = call.execute()) {
			return convert(request, response, handler);
		}
	}

	@Override
	public <T> void request(HttpRequest request, HttpResponse.BodyHandler<T> handler, ResponseCallback<T> callback)
			throws IOException {
		Call call = convert(request);
		Callback enqueue = new Callback() {
			@Override
			public void onFailure(@NotNull Call call, @NotNull IOException e) {
				callback.onError(request, e);
			}

			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) {
				try {
					HttpResponse<T> convert = convert(request, response, handler);
					callback.onResponse(request, convert);
				}
				catch (Throwable e) {
					callback.onError(request, e);
				}
			}
		};
		call.enqueue(enqueue);
	}

	public static class Builder extends HttpDelegateClient.Builder<OkHttpClient, OkHttpDelegateClient, Builder> {

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

		@Override
		protected OkHttpDelegateClient doBuild() {
			OkHttpClient.Builder builder = new OkHttpClient.Builder();
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

			OkHttpClient delegate = builder.build();
			return new OkHttpDelegateClient(delegate);
		}

	}

}
