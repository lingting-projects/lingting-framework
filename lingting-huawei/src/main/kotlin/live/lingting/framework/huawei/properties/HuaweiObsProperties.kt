package live.lingting.framework.huawei.properties;

import live.lingting.framework.aws.s3.AwsS3Properties;

/**
 * @author lingting 2024-09-12 21:25
 */
public class HuaweiObsProperties extends AwsS3Properties {

	public HuaweiObsProperties() {
		setPrefix("obs");
		setEndpoint("myhuaweicloud.com");
	}

	@Override
	public HuaweiObsProperties copy() {
		return fill(new HuaweiObsProperties());
	}

}
