package live.lingting.framework.http;


/**
 * @author lingting 2024-05-07 17:20
 */
@SuppressWarnings("java:S112")
public interface ResponseCallback {

	void onError(HttpRequest request, Throwable e);

	void onResponse(HttpResponse response) throws Throwable;

}
