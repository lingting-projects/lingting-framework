package live.lingting.framework.aws.s3;

import live.lingting.framework.aws.policy.Acl;
import live.lingting.framework.http.api.ApiRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingting 2024-09-19 15:03
 */
@Getter
@Setter
public abstract class AwsS3Request extends ApiRequest {

	protected String key;

	protected Acl acl;

	@Override
	public String path() {
		return key;
	}

	public void setAclIfAbsent(Acl acl) {
		if (this.acl == null) {
			this.acl = acl;
		}
	}

}
