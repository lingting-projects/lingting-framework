package live.lingting.framework.ali;

import live.lingting.framework.aws.policy.Credential;
import live.lingting.framework.aws.policy.Statement;
import live.lingting.framework.util.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2024-09-14 18:15
 */
@EnabledIfSystemProperty(named = "framework.ali.sts.test", matches = "true")
class AliStsTest {

	AliSts sts;

	@BeforeEach
	void before() {
		sts = AliBasic.sts();
	}

	@Test
	void credential() {
		Statement statement = new Statement(true);
		statement.addAction("obs:*");
		statement.addResource("obs:*:*:bucket:*");
		Credential credential = assertDoesNotThrow(() -> sts.credential(statement));
		assertNotNull(credential);
		assertTrue(StringUtils.hasText(credential.getAk()));
		assertTrue(StringUtils.hasText(credential.getSk()));
		assertTrue(StringUtils.hasText(credential.getToken()));
		assertNotNull(credential.getExpire());
	}

}
