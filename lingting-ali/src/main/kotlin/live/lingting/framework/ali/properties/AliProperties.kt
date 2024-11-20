package live.lingting.framework.ali.properties

import live.lingting.framework.aws.s3.AwsS3Properties

/**
 * @author lingting 2024-09-14 14:16
 */
open class AliProperties {
    var scheme: String = "https"

    var prefix: String = ""

    var region: String = ""

    var endpoint: String = "aliyuncs.com"

    var ak: String = ""

    var sk: String = ""

    var token: String? = ""

    open fun host(): String {
        return "$scheme://$prefix.$region.$endpoint"
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
