package live.lingting.framework.ali

import live.lingting.framework.aws.policy.Credential
import live.lingting.framework.aws.policy.Statement
import live.lingting.framework.util.StringUtils
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty

/**
 * @author lingting 2024-09-14 18:15
 */
@EnabledIfSystemProperty(named = "framework.ali.sts.test", matches = "true")
internal class AliStsTest {
    var sts: AliSts? = null

    @BeforeEach
    fun before() {
        sts = AliBasic.sts()
    }

    @Test
    fun credential() {
        val statement = Statement(true)
        statement.addAction("obs:*")
        statement.addResource("obs:*:*:bucket:*")
        val credential = assertDoesNotThrow<Credential> { sts!!.credential(statement) }
        assertNotNull(credential)
        assertTrue(StringUtils.hasText(credential.ak))
        assertTrue(StringUtils.hasText(credential.sk))
        assertTrue(StringUtils.hasText(credential.token))
        assertNotNull(credential.expire)
    }
}
