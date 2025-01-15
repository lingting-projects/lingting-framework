package live.lingting.framework.aws

import java.io.InputStream
import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.s3.AwsS3Meta
import live.lingting.framework.aws.s3.AwsS3MultipartTask
import live.lingting.framework.aws.s3.AwsS3Request
import live.lingting.framework.aws.s3.AwsS3Utils
import live.lingting.framework.aws.s3.interfaces.AwsS3ObjectInterface
import live.lingting.framework.aws.s3.properties.S3Properties
import live.lingting.framework.aws.s3.request.AwsS3MultipartMergeRequest
import live.lingting.framework.aws.s3.request.AwsS3ObjectPutRequest
import live.lingting.framework.aws.s3.request.AwsS3SimpleRequest
import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.jackson.JacksonUtils
import live.lingting.framework.multipart.Multipart
import live.lingting.framework.multipart.Part
import live.lingting.framework.thread.Async

/**
 * @author lingting 2024-09-19 15:09
 */
class AwsS3Object(properties: S3Properties, override val key: String) : AwsS3Client(properties), AwsS3ObjectInterface {

    val publicUrl: String = HttpUrlBuilder.builder().https().host(host).uri(key).build()

    override fun customize(request: AwsS3Request) {
        super<AwsS3Client>.customize(request)
        request.key = key
        request.setAclIfAbsent(acl)
    }

    override fun publicUrl(): String {
        return publicUrl
    }

    override fun head(): AwsS3Meta {
        val request = AwsS3SimpleRequest(HttpMethod.HEAD)
        val response = call(request)
        val headers = response.headers()
        return AwsS3Meta(source = headers)
    }

    override fun put(request: AwsS3ObjectPutRequest) {
        call(request)
    }

    override fun delete() {
        val request = AwsS3SimpleRequest(HttpMethod.DELETE)
        call(request)
    }

    override fun multipartInit(acl: Acl?, meta: HttpHeaders?): String {
        val request = AwsS3SimpleRequest(HttpMethod.POST)
        request.acl = acl
        request.params.add("uploads")
        meta?.run { request.meta.addAll(this) }
        val response = call(request)
        val xml = response.string()
        val node = JacksonUtils.xmlToNode(xml)
        return node["UploadId"].asText()
    }

    override fun multipart(source: InputStream, parSize: Long, async: Async): AwsS3MultipartTask {
        val uploadId = multipartInit()

        val multipart = Multipart.builder()
            .id(uploadId)
            .source(source)
            .partSize(parSize)
            .maxPartCount(AwsS3Utils.MULTIPART_MAX_PART_COUNT)
            .maxPartSize(AwsS3Utils.MULTIPART_MAX_PART_SIZE)
            .minPartSize(AwsS3Utils.MULTIPART_MIN_PART_SIZE)
            .build()

        val task = AwsS3MultipartTask(multipart, async, this)
        task.start()
        return task
    }

    /**
     * 上传分片
     * @return 合并用的 etag
     */
    override fun multipartUpload(uploadId: String, part: Part, input: InputStream): String {
        val request = AwsS3ObjectPutRequest()
        request.stream = input
        request.multipart(uploadId, part)
        val response = call(request)
        val headers = response.headers()
        return headers.etag()!!
    }

    /**
     * 合并分片
     * @param map key: part. value: etag
     */
    override fun multipartMerge(uploadId: String, map: Map<Part, String>) {
        val request = AwsS3MultipartMergeRequest()
        request.uploadId = uploadId
        request.map = map
        call(request)
    }

    override fun multipartCancel(uploadId: String) {
        val request = AwsS3SimpleRequest(HttpMethod.DELETE)
        request.params.add("uploadId", uploadId)
        call(request)
    }
}
