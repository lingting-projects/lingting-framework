package live.lingting.framework.huawei;

import live.lingting.framework.huawei.iam.HuaweiIamToken;
import live.lingting.framework.huawei.properties.HuaweiIamProperties;
import live.lingting.framework.util.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2024-09-13 11:58
 */
class HuaweiIamTest {

	HuaweiIamProperties properties;

	HuaweiIam iam;

	@BeforeEach
	void before() {
		properties = new HuaweiIamProperties();
		String name = System.getenv("HUAWEI_IAM_DOMAIN_NAME");
		assertNotNull(name);
		properties.setDomain(Map.of("name", name));
		properties.setUsername(System.getenv("HUAWEI_IAM_USERNAME"));
		properties.setPassword(System.getenv("HUAWEI_IAM_PASSWORD"));
		assertNotNull(properties.getUsername());
		assertNotNull(properties.getPassword());
		iam = new HuaweiIam(properties);
	}

	@Test
	void token() {
		HuaweiIamToken token = assertDoesNotThrow(() -> iam.token());
		assertNotNull(token);
		assertTrue(StringUtils.hasText(token.getValue()));
		assertNotNull(token.getExpire());
		assertNotNull(token.getIssued());
	}

}
