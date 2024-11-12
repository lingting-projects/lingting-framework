package live.lingting.framework.ali;

import live.lingting.framework.ali.oss.AliOssS3Listener;
import live.lingting.framework.aws.AwsS3Client;
import live.lingting.framework.aws.s3.interfaces.AwsS3Delegation;
import org.slf4j.Logger;

/**
 * @author lingting 2024-09-19 22:05
 */
public abstract class AliOss<C extends AwsS3Client> implements AwsS3Delegation<C> {

	protected final Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

	protected final C client;

	protected AliOss(C client) {
		this.client = client;
		client.setListener(new AliOssS3Listener(client));
	}

	@Override
	public C delegation() {
		return client;
	}

}
