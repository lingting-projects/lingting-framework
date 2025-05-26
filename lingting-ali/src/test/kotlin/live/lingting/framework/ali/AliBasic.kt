package live.lingting.framework.ali

import live.lingting.framework.ali.properties.AliOssProperties
import live.lingting.framework.ali.properties.AliStsProperties
import live.lingting.framework.aws.policy.Acl
import org.junit.jupiter.api.Assertions.assertNotNull

/**
 * @author lingting 2024-09-13 17:18
 */
internal object AliBasic {
    fun sts(): AliSts {
        val properties = AliStsProperties()
        properties.ak = System.getenv("ALI_STS_AK")
        properties.sk = System.getenv("ALI_STS_SK")
        properties.roleArn = System.getenv("ALI_STS_ROLE_ARN")
        properties.roleSessionName = System.getenv("ALI_STS_ROLE_SESSION_NAME")
        assertNotNull(properties.ak)
        assertNotNull(properties.sk)
        assertNotNull(properties.roleArn)
        assertNotNull(properties.roleSessionName)
        return AliSts(properties)
    }

    fun ossProperties(): AliOssProperties {
        val properties = AliOssProperties()
        properties.region = System.getenv("ALI_REGION")
        properties.bucket = System.getenv("ALI_OSS_BUCKET")
        properties.acl = Acl.PUBLIC_READ
        assertNotNull(properties.region)
        assertNotNull(properties.bucket)
        return properties
    }
}
