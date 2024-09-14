package live.lingting.framework.huawei.iam;

import live.lingting.framework.huawei.HuaweiRequest;

/**
 * @author lingting 2024-09-14 15:06
 */
public abstract class HuaweiIamRequest extends HuaweiRequest {

	public boolean usingToken() {
		return true;
	}

}
