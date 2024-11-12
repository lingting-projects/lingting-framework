package live.lingting.framework.huawei;

import live.lingting.framework.aws.AwsS3Object;
import live.lingting.framework.aws.s3.interfaces.AwsS3ObjectDelegation;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.huawei.obs.HuaweiObsHeaders;
import live.lingting.framework.huawei.properties.HuaweiObsProperties;

/**
 * @author lingting 2024-09-13 14:48
 */
public class HuaweiObsObject extends HuaweiObs<AwsS3Object> implements AwsS3ObjectDelegation {

	public HuaweiObsObject(HuaweiObsProperties properties, String key) {
		super(new AwsS3Object(properties, key));
	}

	@Override
	public HuaweiObsHeaders head() {
		HttpHeaders head = AwsS3ObjectDelegation.super.head();
		return new HuaweiObsHeaders(head);
	}

}
