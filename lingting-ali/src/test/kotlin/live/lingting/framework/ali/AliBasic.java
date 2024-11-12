package live.lingting.framework.ali;

import live.lingting.framework.ali.properties.AliOssProperties;
import live.lingting.framework.ali.properties.AliStsProperties;
import live.lingting.framework.aws.policy.Acl;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author lingting 2024-09-13 17:18
 */
class AliBasic {

	static AliSts sts() {
		AliStsProperties properties = new AliStsProperties();
		properties.setRegion(System.getenv("ALI_STS_REGION"));
		properties.setAk(System.getenv("ALI_STS_AK"));
		properties.setSk(System.getenv("ALI_STS_SK"));
		properties.setRoleArn(System.getenv("ALI_STS_ROLE_ARN"));
		properties.setRoleSessionName(System.getenv("ALI_STS_ROLE_SESSION_NAME"));
		assertNotNull(properties.getAk());
		assertNotNull(properties.getSk());
		assertNotNull(properties.getRoleArn());
		assertNotNull(properties.getRoleSessionName());
		return new AliSts(properties);
	}

	static AliOssProperties ossProperties() {
		AliOssProperties properties = new AliOssProperties();
		properties.setRegion(System.getenv("ALI_STS_REGION"));
		properties.setBucket(System.getenv("ALI_OSS_BUCKET"));
		properties.setAcl(Acl.PUBLIC_READ);
		assertNotNull(properties.getRegion());
		assertNotNull(properties.getBucket());
		return properties;
	}

}
