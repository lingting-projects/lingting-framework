package live.lingting.framework.ali;

import live.lingting.framework.ali.properties.AliOssProperties;
import live.lingting.framework.aws.AwsS3Bucket;
import live.lingting.framework.aws.s3.interfaces.AwsS3BucketDelegation;

/**
 * @author lingting 2024-09-19 21:21
 */
public class AliOssBucket extends AliOss<AwsS3Bucket> implements AwsS3BucketDelegation {

	protected final AliOssProperties ossProperties;

	public AliOssBucket(AliOssProperties properties) {
		super(new AwsS3Bucket(properties.s3()));
		this.ossProperties = properties;
	}

	@Override
	public AliOssObject use(String key) {
		return new AliOssObject(ossProperties, key);
	}

}
