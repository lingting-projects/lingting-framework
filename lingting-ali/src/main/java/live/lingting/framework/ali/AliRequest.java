package live.lingting.framework.ali;

import live.lingting.framework.http.api.ApiRequest;
import live.lingting.framework.id.Snowflake;

/**
 * @author lingting 2024-09-14 13:49
 */
public abstract class AliRequest extends ApiRequest {

	protected Snowflake snowflake = new Snowflake(5, 7);

	public abstract String name();

	public abstract String version();

	public String contentType() {
		return "application/json;charset=utf8";
	}

	protected String nonce() {
		return snowflake.nextStr();
	}

}
