package live.lingting.framework.ali.oss;

import live.lingting.framework.ali.AliRequest;
import live.lingting.framework.http.HttpUrlBuilder;
import live.lingting.framework.s3.Acl;
import live.lingting.framework.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingting 2024-09-18 13:58
 */
@Getter
@Setter
public abstract class AliOssRequest extends AliRequest {

	protected Acl acl;

	protected String key;

	@Override
	public String contentType() {
		return null;
	}

	@Override
	public void configure(HttpUrlBuilder builder) {
		if (StringUtils.hasText(key)) {
			builder.uri(key);
		}
	}

	public void setAclIfAbsent(Acl acl) {
		if (this.acl == null) {
			this.acl = acl;
		}
	}

}
