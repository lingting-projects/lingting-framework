package live.lingting.framework.aws.s3

import live.lingting.framework.aws.s3.properties.S3Properties

/**
 * @author lingting 2024-09-12 21:20
 */
open class AwsS3Properties : S3Properties() {

    override fun host(): String {
        return "$scheme://$bucket.s3.$region.$endpoint"
    }

}
