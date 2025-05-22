package live.lingting.framework.aws.s3.request

import live.lingting.framework.aws.s3.AwsS3Request
import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.body.BodySource
import live.lingting.framework.multipart.Part
import java.io.InputStream

/**
 * @author lingting 2024-09-13 16:31
 */
open class AwsS3ObjectPutRequest : AwsS3Request() {

    var stream: InputStream? = null

    var uploadId: String? = null
        protected set

    var part: Part? = null
        protected set

    fun multipart(id: String, part: Part) {
        this.uploadId = id
        this.part = part
    }

    override fun method(): HttpMethod {
        return HttpMethod.PUT
    }

    override fun body(): BodySource {
        return BodySource.of(stream!!)
    }

    override fun onCall() {
        headers.contentType("application/octet-stream")
        part?.run {
            headers.contentLength(size.bytes)
        }
    }

    override fun onParams() {
        val p = part
        if (p != null) {
            val u = uploadId
            params.add("partNumber", (p.index + 1).toString())
            checkNotNull(u) { "uploadId must be not null!" }
            params.add("uploadId", u)
        }
    }

}
