package live.lingting.framework.huawei;

import live.lingting.framework.aws.policy.Credential;
import live.lingting.framework.huawei.properties.HuaweiIamProperties;
import live.lingting.framework.util.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2024-09-13 11:58
 */
@EnabledIfSystemProperty(named = "framework.huawei.iam.test", matches = "true")
class HuaweiIamTest {

	HuaweiIam iam;

	HuaweiIamProperties properties;

	@BeforeEach
	void before() {
		iam = HuaweiBasic.iam();
		properties = iam.properties;
	}

	@Test
	void credential() {
		HuaweiStatement statement = new HuaweiStatement(true);
		statement.addAction("obs:*");
		statement.addResource("obs:*:*:bucket:*");
		Credential credential = assertDoesNotThrow(() -> iam.credential(statement));
		assertNotNull(credential);
		assertTrue(StringUtils.hasText(credential.getAk()));
		assertTrue(StringUtils.hasText(credential.getSk()));
		assertTrue(StringUtils.hasText(credential.getToken()));
		assertNotNull(credential.getExpire());
	}

}
