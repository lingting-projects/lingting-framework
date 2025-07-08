package live.lingting.framework.aws.s3.request

import live.lingting.framework.aws.s3.AwsS3Request
import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.body.Body
import live.lingting.framework.http.body.MemoryBody

/**
 * @author lingting 2024-09-13 16:54
 */
class AwsS3MultipartMergeRequest : AwsS3Request() {

    var uploadId: String? = null

    /**
     * key: 分片索引(0开始)
     * value: 分片上传后返回的 eTag
     */
    var eTagMap: Map<Long, String> = emptyMap()

    override fun method(): HttpMethod {
        return HttpMethod.POST
    }

    override fun body(): Body {
        val builder = StringBuilder("<CompleteMultipartUpload>\n")
        eTagMap.keys.sorted().forEach { index ->
            val e = eTagMap[index]
            builder.append("<Part><PartNumber>")
                .append(index + 1)
                .append("</PartNumber><ETag>")
                .append(e)
                .append("</ETag></Part>\n")
        }

        builder.append("</CompleteMultipartUpload>")
        return MemoryBody(builder.toString())
    }

    override fun onParams() {
        params.add("uploadId", uploadId!!)
    }

}
