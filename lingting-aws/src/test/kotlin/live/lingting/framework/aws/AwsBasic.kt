package live.lingting.framework.aws

import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.properties.AwsS3Properties
import live.lingting.framework.aws.properties.AwsStsProperties
import org.junit.jupiter.api.Assertions.assertNotNull

/**
 * @author lingting 2024-09-13 17:18
 */
internal object AwsBasic {

    fun sts(): AwsSts {
        val properties = AwsStsProperties()
        properties.ak = System.getenv("AWS_STS_AK")
        properties.sk = System.getenv("AWS_STS_SK")
        properties.roleArn = System.getenv("AWS_STS_ROLE_ARN")
        properties.roleSessionName = System.getenv("AWS_STS_ROLE_SESSION_NAME")
        properties.sourceIdentity = System.getenv("AWS_STS_SourceIdentity") ?: ""
        assertNotNull(properties.ak)
        assertNotNull(properties.sk)
        assertNotNull(properties.roleArn)
        assertNotNull(properties.roleSessionName)
        return AwsSts(properties)
    }

    fun s3Properties(): AwsS3Properties {
        val properties = AwsS3Properties()
        properties.region = System.getenv("AWS_REGION")
        properties.bucket = System.getenv("AWS_OSS_BUCKET")
        properties.acl = Acl.PUBLIC_READ
        assertNotNull(properties.region)
        assertNotNull(properties.bucket)
        return properties
    }
}
