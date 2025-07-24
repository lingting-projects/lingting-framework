package live.lingting.framework.aws.s3.impl

import live.lingting.framework.data.DataSize
import live.lingting.framework.multipart.Part

/**
 * @author lingting 2025/7/8 10:07
 */
class AwsS3PreSignedMultipart(
    /**
     * 文件大小
     */
    val size: DataSize,
    /**
     * 上传id
     */
    val uploadId: String,
    /**
     * 分片大小
     */
    val partSize: DataSize,
    /**
     * 每片的预签名
     */
    val items: List<Item>,
) {

    class Item(
        val part: Part,
        val url: String,
        val headers: Map<String, List<String>>
    ) {}

}
