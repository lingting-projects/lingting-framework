package live.lingting.framework.aws.s3

import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.policy.Credential

/**
 * @author lingting 2024-09-12 21:20
 */
open class AwsS3Properties {
    @JvmField
    var scheme: String = "https"

    @JvmField
    var prefix: String = "s3"

    @JvmField
    var connector: String = "."

    @JvmField
    var region: String? = null

    @JvmField
    var endpoint: String = "amazonaws.com"

    @JvmField
    var bucket: String? = null

    @JvmField
    var acl: Acl = Acl.PRIVATE

    @JvmField
    var ak: String? = null

    @JvmField
    var sk: String? = null

    @JvmField
    var token: String? = null

    fun <T : AwsS3Properties?> fill(properties: T): T {
        properties!!.scheme = scheme
        properties.region = region
        properties.endpoint = endpoint
        properties.bucket = bucket
        properties.acl = acl
        properties.ak = ak
        properties.sk = sk
        properties.token = token
        return properties
    }

    open fun copy(): AwsS3Properties? {
        val properties = AwsS3Properties()
        fill(properties)
        return properties
    }

    fun useCredential(credential: Credential) {
        ak = credential.ak
        sk = credential.sk
        token = credential.token
    }

    fun host(): String {
        return "%s://%s.%s%s%s.%s".formatted(scheme, bucket, prefix, connector, region, endpoint)
    }
}
