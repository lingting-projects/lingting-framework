package live.lingting.framework.huawei;

import live.lingting.framework.aws.AwsS3Bucket;
import live.lingting.framework.aws.s3.interfaces.AwsS3BucketDelegation;
import live.lingting.framework.huawei.properties.HuaweiObsProperties;

/**
 * @author lingting 2024-09-13 14:48
 */
public class HuaweiObsBucket extends HuaweiObs<AwsS3Bucket> implements AwsS3BucketDelegation {

	protected final HuaweiObsProperties properties;

	public HuaweiObsBucket(HuaweiObsProperties properties) {
		super(new AwsS3Bucket(properties));
		this.properties = properties;
	}

	public HuaweiObsObject use(String key) {
		return new HuaweiObsObject(properties, key);
	}

}
