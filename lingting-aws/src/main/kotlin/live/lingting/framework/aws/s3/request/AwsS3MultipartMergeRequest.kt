package live.lingting.framework.aws.s3.request

import live.lingting.framework.aws.s3.AwsS3Request
import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.body.BodySource
import live.lingting.framework.http.body.MemoryBody
import live.lingting.framework.multipart.Part

/**
 * @author lingting 2024-09-13 16:54
 */
class AwsS3MultipartMergeRequest : AwsS3Request() {
    var uploadId: String? = null

    var map: Map<Part, String> = emptyMap()

    override fun method(): HttpMethod {
        return HttpMethod.POST
    }

    override fun body(): BodySource {
        val builder = StringBuilder("<CompleteMultipartUpload>\n")

        map.keys.stream().sorted(Comparator.comparing(Part::index)).forEach { p ->
            val e = map[p]
            builder.append("<Part><PartNumber>")
                .append(p.index + 1)
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
