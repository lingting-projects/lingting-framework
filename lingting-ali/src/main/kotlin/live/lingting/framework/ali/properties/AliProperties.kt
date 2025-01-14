package live.lingting.framework.ali.properties

import live.lingting.framework.aws.s3.properties.S3Properties

/**
 * @author lingting 2024-09-14 14:16
 */
abstract class AliProperties : S3Properties() {

    init {
        endpoint = "aliyuncs.com"
    }

}
