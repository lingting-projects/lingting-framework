package live.lingting.framework.ali;

import live.lingting.framework.ali.properties.AliProperties;
import live.lingting.framework.http.api.ApiClient;

import static live.lingting.framework.util.HttpUtils.HEADER_HOST;

/**
 * @author lingting 2024-09-14 13:49
 */
@SuppressWarnings("java:S112")
public abstract class AliClient<R extends AliRequest> extends ApiClient<R> {


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



}
