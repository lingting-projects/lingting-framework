package live.lingting.framework.ali.properties

import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.policy.Credential
import live.lingting.framework.aws.s3.AwsS3Properties

/**
 * @author lingting 2024-09-18 10:29
 */
class AliOssProperties : AliProperties() {
    var bucket: String = ""

    var acl: Acl = Acl.PRIVATE

    init {
        prefix = "oss"
    }

    override fun host(): String {
        return "$scheme://$bucket.$prefix-$region.$endpoint"
    }

    override fun s3(): AwsS3Properties {
        val s3 = super.s3()
        s3.prefix = "oss"
        s3.bucket = bucket
        s3.acl = acl
        return s3
    }

    fun copy(): AliOssProperties {
        val properties = AliOssProperties()
        properties.scheme = scheme
        properties.prefix = prefix
        properties.region = region
        properties.endpoint = endpoint
        properties.bucket = bucket
        properties.acl = acl

        properties.ak = ak
        properties.sk = sk
        properties.token = token
        return properties
    }

    fun useCredential(credential: Credential) {
        ak = credential.ak
        sk = credential.sk
        token = credential.token
    }
}
