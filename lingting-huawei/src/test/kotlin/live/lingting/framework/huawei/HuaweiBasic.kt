package live.lingting.framework.huawei;

import live.lingting.framework.aws.policy.Acl;
import live.lingting.framework.huawei.properties.HuaweiIamProperties;
import live.lingting.framework.huawei.properties.HuaweiObsProperties;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author lingting 2024-09-13 17:18
 */
class HuaweiBasic {

	static HuaweiIam iam() {
		HuaweiIamProperties properties = new HuaweiIamProperties();
		String name = System.getenv("HUAWEI_IAM_DOMAIN_NAME");
		assertNotNull(name);
		properties.setDomain(Map.of("name", name));
		properties.username = System.getenv("HUAWEI_IAM_USERNAME");
		properties.password = System.getenv("HUAWEI_IAM_PASSWORD");
		assertNotNull(properties.username);
		assertNotNull(properties.password);
		HuaweiIam iam = new HuaweiIam(properties);
		iam.refreshToken();
		return iam;
	}

	static HuaweiObsProperties obsProperties() {
		HuaweiObsProperties properties = new HuaweiObsProperties();
		properties.region = System.getenv("HUAWEI_OBS_REGION");
		properties.bucket = System.getenv("HUAWEI_OBS_BUCKET");
		properties.acl = Acl.PUBLIC_READ;
		assertNotNull(properties.region);
		assertNotNull(properties.bucket);
		return properties;
	}

}
