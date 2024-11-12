package live.lingting.framework.huawei;

import live.lingting.framework.aws.AwsS3Client;
import live.lingting.framework.aws.s3.interfaces.AwsS3Delegation;
import live.lingting.framework.huawei.obs.HuaweiObsS3Listener;

/**
 * @author lingting 2024-09-13 13:45
 */

public abstract class HuaweiObs<C extends AwsS3Client> implements AwsS3Delegation<C> {

	public static final String HEADER_PREFIX = "x-obs";

	protected final C client;

	protected HuaweiObs(C client) {
		this.client = client;
		client.setListener(new HuaweiObsS3Listener(client));
	}

	@Override
	public C delegation() {
		return client;
	}

}
