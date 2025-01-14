package live.lingting.framework.ali.properties

import live.lingting.framework.aws.s3.AwsS3Properties

/**
 * @author lingting 2024-09-14 14:16
 */
abstract class AliProperties : AwsS3Properties() {

    init {
        endpoint = "aliyuncs.com"
    }

}
