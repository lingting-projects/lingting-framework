package live.lingting.framework.ali

import live.lingting.framework.ali.properties.AliOssProperties
import live.lingting.framework.ali.properties.AliStsProperties
import live.lingting.framework.aws.policy.Acl
import org.junit.jupiter.api.Assertions

/**
 * @author lingting 2024-09-13 17:18
 */
internal object AliBasic {
    fun sts(): AliSts {
        val properties = AliStsProperties()
        properties.region = System.getenv("ALI_STS_REGION")
        properties.ak = System.getenv("ALI_STS_AK")
        properties.sk = System.getenv("ALI_STS_SK")
        properties.roleArn = System.getenv("ALI_STS_ROLE_ARN")
        properties.roleSessionName = System.getenv("ALI_STS_ROLE_SESSION_NAME")
        Assertions.assertNotNull(properties.ak)
        Assertions.assertNotNull(properties.sk)
        Assertions.assertNotNull(properties.roleArn)
        Assertions.assertNotNull(properties.roleSessionName)
        return AliSts(properties)
    }

    fun ossProperties(): AliOssProperties {
        val properties = AliOssProperties()
        properties.region = System.getenv("ALI_STS_REGION")
        properties.bucket = System.getenv("ALI_OSS_BUCKET")
        properties.acl = Acl.PUBLIC_READ
        Assertions.assertNotNull(properties.region)
        Assertions.assertNotNull(properties.bucket)
        return properties
    }
}
