package live.lingting.framework.ali.sts;

import live.lingting.framework.ali.AliRequest;
import live.lingting.framework.id.Snowflake;
import live.lingting.framework.util.StringUtils;
import live.lingting.framework.value.WaitValue;


/**
 * @author lingting 2024-09-18 16:51
 */
public abstract class AliStsRequest extends AliRequest {

	protected Snowflake snowflake = new Snowflake(5, 7);

	protected final WaitValue<String> nonceValue = WaitValue.of();

	public abstract String name();

	public abstract String version();


	public String nonce() {
		return nonceValue.compute(v -> {
			if (StringUtils.hasText(v)) {
				return v;
			}
			return snowflake.nextStr();
		});
	}

}
