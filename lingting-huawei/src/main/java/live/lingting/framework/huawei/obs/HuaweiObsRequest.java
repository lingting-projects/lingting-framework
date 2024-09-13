package live.lingting.framework.huawei.obs;

import live.lingting.framework.http.HttpUrlBuilder;
import live.lingting.framework.huawei.iam.HuaweiIamRequest;
import live.lingting.framework.s3.Acl;
import live.lingting.framework.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingting 2024-09-13 13:45
 */
@Getter
@Setter
public abstract class HuaweiObsRequest extends HuaweiIamRequest {

	protected Acl acl;

	protected String key;

	public void setAclIfAbsent(Acl acl) {
		if (this.acl == null) {
			this.acl = acl;
		}
	}

	@Override
	public void configure(HttpUrlBuilder builder) {
		if (StringUtils.hasText(key)) {
			builder.uri(key);
		}
	}

}
