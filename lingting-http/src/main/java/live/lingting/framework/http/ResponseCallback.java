package live.lingting.framework.http;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author lingting 2024-05-07 17:20
 */
@SuppressWarnings("java:S112")
public interface ResponseCallback<T> {

	void onError(HttpRequest request, Throwable e);

	void onResponse(HttpRequest request, HttpResponse<T> response) throws Throwable;

}
