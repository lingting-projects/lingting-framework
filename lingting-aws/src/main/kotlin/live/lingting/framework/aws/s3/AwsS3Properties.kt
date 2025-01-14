package live.lingting.framework.aws.s3

import live.lingting.framework.aws.policy.Credential
import live.lingting.framework.aws.s3.properties.S3Properties

/**
 * @author lingting 2024-09-12 21:20
 */
open class AwsS3Properties : S3Properties() {

    var region: String = ""

    var token: String? = ""

    override fun fill(properties: S3Properties) {
        super.fill(properties)
        if (properties is AwsS3Properties) {
            properties.region = region
            properties.token = token
        }
    }

    open fun copy(): AwsS3Properties {
        val properties = AwsS3Properties()
        fill(properties)
        return properties
    }

    fun useCredential(credential: Credential) {
        ak = credential.ak
        sk = credential.sk
        token = credential.token
    }

    override fun host(): String {
        return "$scheme://$bucket.s3.$region.$endpoint"
    }
}
