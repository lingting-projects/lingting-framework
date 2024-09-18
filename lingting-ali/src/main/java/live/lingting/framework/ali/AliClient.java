package live.lingting.framework.ali;

import live.lingting.framework.ali.properties.AliProperties;
import live.lingting.framework.http.HttpUrlBuilder;
import live.lingting.framework.http.api.ApiClient;
import live.lingting.framework.http.header.HttpHeaders;
import lombok.SneakyThrows;

import java.net.http.HttpRequest;

import static live.lingting.framework.util.HttpUtils.HEADER_HOST;

/**
 * @author lingting 2024-09-14 13:49
 */
@SuppressWarnings("java:S112")
public abstract class AliClient<R extends AliRequest> extends ApiClient<R> {

	public static final String BODY_EMPTY = "UNSIGNED-PAYLOAD";

	protected static final String[] HEADER_INCLUDE = { HEADER_HOST, "content-type", "content-md5" };

	protected final String ak;

	protected final String sk;

	protected final String token;

	protected AliClient(AliProperties properties) {
		super(properties.host());
		this.ak = properties.getAk();
		this.sk = properties.getSk();
		this.token = properties.getToken();
	}

	protected abstract void authorization(R request, HttpHeaders headers, HttpRequest.Builder builder,
			HttpUrlBuilder urlBuilder) throws Exception;

	@SneakyThrows
	@Override
	protected void configure(R request, HttpHeaders headers, HttpRequest.Builder builder, HttpUrlBuilder urlBuilder) {
		authorization(request, headers, builder, urlBuilder);
	}

}
