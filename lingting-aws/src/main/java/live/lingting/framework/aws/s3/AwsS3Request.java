package live.lingting.framework.aws.s3;

import live.lingting.framework.aws.policy.Acl;
import live.lingting.framework.http.api.ApiRequest;

/**
 * @author lingting 2024-09-19 15:03
 */
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

	public String getKey() {return this.key;}

	public Acl getAcl() {return this.acl;}

	public void setKey(String key) {this.key = key;}

	public void setAcl(Acl acl) {this.acl = acl;}
}
