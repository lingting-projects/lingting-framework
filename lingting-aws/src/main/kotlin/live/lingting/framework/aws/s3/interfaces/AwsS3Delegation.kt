package live.lingting.framework.aws.s3.interfaces

import live.lingting.framework.aws.AwsS3Client

/**
 * @author lingting 2024-09-19 22:06
 */
interface AwsS3Delegation<C : AwsS3Client?> {
    fun delegation(): C
}
