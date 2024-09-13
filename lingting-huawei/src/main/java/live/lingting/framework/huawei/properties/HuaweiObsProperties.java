package live.lingting.framework.huawei.properties;

import live.lingting.framework.s3.S3Properties;

/**
 * @author lingting 2024-09-12 21:25
 */
public class HuaweiObsProperties extends S3Properties {

	public HuaweiObsProperties() {
		setEndpoint("myhuaweicloud.com");
	}

	@Override
	public HuaweiObsProperties copy() {
		return fill(new HuaweiObsProperties());
	}

}
