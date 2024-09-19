package live.lingting.framework.huawei.obs;

import live.lingting.framework.aws.policy.Acl;
import live.lingting.framework.huawei.HuaweiRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingting 2024-09-13 13:45
 */
@Getter
@Setter
public abstract class HuaweiObsRequest extends HuaweiRequest {

	protected Acl acl;

	protected String key;

	public void setAclIfAbsent(Acl acl) {
		if (this.acl == null) {
			this.acl = acl;
		}
	}

	@Override
	public String path() {
		return key;
	}

}
