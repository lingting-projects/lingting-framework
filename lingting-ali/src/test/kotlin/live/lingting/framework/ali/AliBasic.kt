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
        properties.ak = System.getenv("AK")
        properties.sk = System.getenv("SK")
        properties.region = System.getenv("REGION")
        properties.roleArn = System.getenv("ROLE_ARN")
        properties.roleSessionName = System.getenv("ROLE_SESSION_NAME")
        assertNotNull(properties.ak)
        assertNotNull(properties.sk)
        assertNotNull(properties.roleArn)
        assertNotNull(properties.roleSessionName)
        return AliSts(properties)
    }

    fun ossStsProperties(): AliOssProperties {
        val properties = AliOssProperties()
        properties.region = System.getenv("REGION")
        properties.bucket = System.getenv("BUCKET")
        properties.acl = Acl.PUBLIC_READ
        assertNotNull(properties.region)
        assertNotNull(properties.bucket)
        return properties
    }

    fun ossProperties(): AliOssProperties {
        val properties = AliOssProperties()
        properties.ak = System.getenv("AK")
        properties.sk = System.getenv("SK")
        properties.region = System.getenv("REGION")
        properties.bucket = System.getenv("BUCKET")
        properties.acl = Acl.PUBLIC_READ
        assertNotNull(properties.region)
        assertNotNull(properties.bucket)
        return properties
    }

}
