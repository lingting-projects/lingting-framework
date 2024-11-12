package live.lingting.framework.ali.properties

import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.policy.Credential
import live.lingting.framework.aws.s3.AwsS3Properties

/**
 * @author lingting 2024-09-18 10:29
 */
class AliOssProperties : AliProperties() {
    @JvmField
    var bucket: String? = null

    @JvmField
    var acl: Acl = Acl.PRIVATE

    init {
        setPrefix("oss")
    }

    override fun host(): String {
        return "%s://%s.%s-%s.%s".formatted(scheme, bucket, prefix, region, endpoint)
    }

    override fun s3(): AwsS3Properties {
        val s3 = super.s3()
        s3!!.prefix = "oss"
        s3.bucket = bucket
        s3.acl = acl
        return s3
    }

    fun copy(): AliOssProperties {
        val properties = AliOssProperties()
        properties.setScheme(getScheme())
        properties.setPrefix(getPrefix())
        properties.setRegion(getRegion())
        properties.setEndpoint(getEndpoint())
        properties.bucket = bucket
        properties.acl = acl

        properties.setAk(getAk())
        properties.setSk(getSk())
        properties.setToken(getToken())
        return properties
    }

    fun useCredential(credential: Credential) {
        setAk(credential.ak)
        setSk(credential.sk)
        setToken(credential.token)
    }
}
