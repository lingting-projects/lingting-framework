package live.lingting.framework.aws.s3.properties

import live.lingting.framework.aws.policy.Acl

/**
 * @author lingting 2025/1/14 17:14
 */
abstract class S3Properties {

    var scheme: String = "https"

    var endpoint: String = "amazonaws.com"

    var bucket: String = ""

    var acl: Acl = Acl.PRIVATE

    var ak: String = ""

    var sk: String = ""

    open fun fill(properties: S3Properties) {
        properties.endpoint = endpoint
        properties.bucket = bucket
        properties.acl = acl
        properties.ak = ak
        properties.sk = sk
    }

    abstract fun host(): String

}
