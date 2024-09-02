package live.lingting.framework.http.okhttp;

import live.lingting.framework.http.OkHttpClient;
import live.lingting.framework.http.ResponseCallback;
import lombok.RequiredArgsConstructor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author lingting 2024-09-02 15:50
 */
@RequiredArgsConstructor
public class OkHttpResponseCallback<T> implements Callback {

	private final HttpRequest request;

	private final HttpResponse.BodyHandler<T> handler;

	private final ResponseCallback<T> callback;

	@Override
	public void onFailure(@NotNull Call call, @NotNull IOException e) {
		callback.onError(request, e);
	}

	@Override
	public void onResponse(@NotNull Call call, @NotNull Response response) {
		try {
			HttpResponse<T> convert = OkHttpClient.convert(request, response, handler);
			callback.onResponse(request, convert);
		}
		catch (Throwable e) {
			callback.onError(request, e);
		}
	}

}
