package live.lingting.framework.ali.properties

import live.lingting.framework.aws.s3.AwsS3Properties

/**
 * @author lingting 2024-09-14 14:16
 */
open class AliProperties {
    var scheme: String = "https"

    var prefix: String? = null

    @JvmField
    var region: String? = null

    var endpoint: String = "aliyuncs.com"

    @JvmField
    var ak: String? = null

    @JvmField
    var sk: String? = null

    var token: String? = null

    open fun host(): String {
        return "%s://%s.%s.%s".formatted(scheme, prefix, region, endpoint)
    }

    open fun s3(): AwsS3Properties {
        val s3 = AwsS3Properties()
        s3.scheme = scheme
        s3.connector = "-"
        s3.region = region
        s3.endpoint = endpoint
        s3.ak = ak
        s3.sk = sk
        s3.token = token
        return s3
    }
}
