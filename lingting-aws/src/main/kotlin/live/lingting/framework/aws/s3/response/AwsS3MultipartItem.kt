package live.lingting.framework.aws.s3.response

/**
 * @author lingting 2024-09-19 20:45
 */
@JvmRecord
data class AwsS3MultipartItem(@JvmField val key: String, @JvmField val uploadId: String)
