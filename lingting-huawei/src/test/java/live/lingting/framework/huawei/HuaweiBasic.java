package live.lingting.framework.huawei;

import live.lingting.framework.huawei.properties.HuaweiIamProperties;
import live.lingting.framework.huawei.properties.HuaweiObsProperties;
import live.lingting.framework.s3.Acl;

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
		properties.setUsername(System.getenv("HUAWEI_IAM_USERNAME"));
		properties.setPassword(System.getenv("HUAWEI_IAM_PASSWORD"));
		assertNotNull(properties.getUsername());
		assertNotNull(properties.getPassword());
		HuaweiIam iam = new HuaweiIam(properties);
		iam.refreshToken();
		return iam;
	}

	static HuaweiObsProperties obsProperties() {
		HuaweiObsProperties properties = new HuaweiObsProperties();
		properties.setRegion(System.getenv("HUAWEI_OBS_REGION"));
		properties.setBucket(System.getenv("HUAWEI_OBS_BUCKET"));
		properties.setAcl(Acl.PUBLIC_READ);
		assertNotNull(properties.getRegion());
		assertNotNull(properties.getBucket());
		return properties;
	}

}
