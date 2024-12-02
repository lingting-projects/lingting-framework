package live.lingting.framework.huawei

import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.huawei.properties.HuaweiIamProperties
import live.lingting.framework.huawei.properties.HuaweiObsProperties
import org.junit.jupiter.api.Assertions.assertNotNull

/**
 * @author lingting 2024-09-13 17:18
 */
internal object HuaweiBasic {
    fun iam(): HuaweiIam {
        val properties = HuaweiIamProperties()
        val name = System.getenv("HUAWEI_IAM_DOMAIN_NAME")
        assertNotNull(name)
        properties.domain = mapOf("name" to name)
        properties.username = System.getenv("HUAWEI_IAM_USERNAME")
        properties.password = System.getenv("HUAWEI_IAM_PASSWORD")
        assertNotNull(properties.username)
        assertNotNull(properties.password)
        val iam = HuaweiIam(properties)
        iam.refreshToken()
        return iam
    }

    fun obsProperties(): HuaweiObsProperties {
        val properties = HuaweiObsProperties()
        properties.region = System.getenv("HUAWEI_OBS_REGION")
        properties.bucket = System.getenv("HUAWEI_OBS_BUCKET")
        properties.acl = Acl.PUBLIC_READ
        assertNotNull(properties.region)
        assertNotNull(properties.bucket)
        return properties
    }
}
