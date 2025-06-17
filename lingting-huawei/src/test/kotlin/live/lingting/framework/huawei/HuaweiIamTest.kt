package live.lingting.framework.huawei

import live.lingting.framework.aws.policy.Credential
import live.lingting.framework.aws.policy.Statement
import live.lingting.framework.huawei.properties.HuaweiIamProperties
import live.lingting.framework.util.StringUtils.hasText
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty

/**
 * @author lingting 2024-09-13 11:58
 */
@EnabledIfSystemProperty(named = "framework.huawei.iam.test", matches = "true")
internal class HuaweiIamTest {
    var iam: HuaweiIam? = null

    var properties: HuaweiIamProperties? = null

    @BeforeEach
    fun before() {
        iam = HuaweiBasic.iam()
        properties = iam!!.properties
    }

    @Test
    fun credential() {
        val statement = Statement(true)
        statement.addAction("obs:*")
        statement.addResource("obs:*:*:bucket:*")
        val credential = assertDoesNotThrow<Credential?> { iam!!.credential(statement) }
        assertNotNull(credential)
        assertTrue(hasText(credential!!.ak))
        assertTrue(hasText(credential.sk))
        assertTrue(hasText(credential.token))
        assertNotNull(credential.expire)
    }
}
