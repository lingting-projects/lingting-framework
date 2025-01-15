package live.lingting.framework.aws.s3.properties

import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.policy.Credential
import live.lingting.framework.aws.s3.AwsS3Properties

/**
 * @author lingting 2025/1/14 17:14
 */
abstract class S3Properties {

    var scheme: String = "https"

    var region: String = ""

    var endpoint: String = "amazonaws.com"

    var bucket: String = ""

    var acl: Acl = Acl.PRIVATE

    var ak: String = ""

    var sk: String = ""

    var token: String? = ""

    open fun useCredential(credential: Credential) {
        ak = credential.ak
        sk = credential.sk
        token = credential.token
    }

    open fun from(properties: S3Properties) {
        scheme = properties.scheme
        region = properties.region
        endpoint = properties.endpoint
        bucket = properties.bucket
        acl = properties.acl
        ak = properties.ak
        sk = properties.sk
        token = properties.token
    }

    open fun copy(): S3Properties {
        return AwsS3Properties().also { it.from(this) }
    }

    abstract fun host(): String

}
