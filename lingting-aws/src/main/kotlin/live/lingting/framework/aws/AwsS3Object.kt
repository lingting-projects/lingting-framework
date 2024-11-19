package live.lingting.framework.aws

import java.io.File
import java.io.InputStream
import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.s3.AwsS3MultipartTask
import live.lingting.framework.aws.s3.AwsS3Properties
import live.lingting.framework.aws.s3.AwsS3Request
import live.lingting.framework.aws.s3.AwsS3Utils
import live.lingting.framework.aws.s3.interfaces.AwsS3ObjectInterface
import live.lingting.framework.aws.s3.request.AwsS3MultipartMergeRequest
import live.lingting.framework.aws.s3.request.AwsS3ObjectPutRequest
import live.lingting.framework.aws.s3.request.AwsS3SimpleRequest
import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.jackson.JacksonUtils
import live.lingting.framework.multipart.Multipart
import live.lingting.framework.multipart.Part
import live.lingting.framework.stream.CloneInputStream
import live.lingting.framework.stream.FileCloneInputStream
import live.lingting.framework.thread.Async

/**
 * @author lingting 2024-09-19 15:09
 */
class AwsS3Object(properties: AwsS3Properties, override val key: String?) : AwsS3Client(properties), AwsS3ObjectInterface {
    // endregion
    val publicUrl: String = HttpUrlBuilder.builder().https().host(host).uri(key!!).build()

    override fun customize(request: AwsS3Request) {
        request.key = key
        request.setAclIfAbsent(acl)
    }

    // region get
    override fun publicUrl(): String? {
        return publicUrl
    }

    override fun head(): HttpHeaders {
        val request = AwsS3SimpleRequest(HttpMethod.HEAD)
        val response = call(request)
        return response.headers()
    }

    // endregion
    // region put

    override fun put(file: File?) {
        put(file, null)
    }


    override fun put(file: File?, acl: Acl?) {
        put(FileCloneInputStream(file!!), acl)
    }


    override fun put(`in`: InputStream) {
        put(`in`, null)
    }


    override fun put(`in`: InputStream, acl: Acl?) {
        put(FileCloneInputStream(`in`), acl)
    }

    override fun put(`in`: CloneInputStream) {
        put(`in`, null)
    }

    override fun put(`in`: CloneInputStream, acl: Acl?) {
        `in`.use {
            val request = AwsS3ObjectPutRequest()
            request.stream = `in`
            request.acl = acl
            call(request)
        }
    }

    override fun delete() {
        val request = AwsS3SimpleRequest(HttpMethod.DELETE)
        call(request)
    }

    // endregion
    // region multipart
    override fun multipartInit(): String? {
        return multipartInit(null)
    }

    override fun multipartInit(acl: Acl?): String? {
        val request = AwsS3SimpleRequest(HttpMethod.POST)
        request.acl = acl
        request.params.add("uploads")
        val response = call(request)
        val xml = response.string()
        val node = JacksonUtils.xmlToNode(xml)
        return node["UploadId"].asText()
    }


    override fun multipart(source: InputStream?): AwsS3MultipartTask? {
        return multipart(source, AwsS3Utils.Companion.MULTIPART_DEFAULT_PART_SIZE, Async(20))
    }


    override fun multipart(source: InputStream?, parSize: Long, async: Async): AwsS3MultipartTask {
        val uploadId = multipartInit()

        val multipart = Multipart.builder()
            .id(uploadId!!)
            .source(source!!)
            .partSize(parSize)
            .maxPartCount(AwsS3Utils.Companion.MULTIPART_MAX_PART_COUNT)
            .maxPartSize(AwsS3Utils.Companion.MULTIPART_MAX_PART_SIZE)
            .minPartSize(AwsS3Utils.Companion.MULTIPART_MIN_PART_SIZE)
            .build()

        val task = AwsS3MultipartTask(multipart, async, this)
        task.start()
        return task
    }

    /**
     * 上传分片
     *
     * @return 合并用的 etag
     */
    override fun multipartUpload(uploadId: String?, part: Part?, `in`: InputStream?): String? {
        val request = AwsS3ObjectPutRequest()
        request.stream = `in`
        request.multipart(uploadId, part)
        val response = call(request)
        val headers = response.headers()
        return headers.etag()
    }

    /**
     * 合并分片
     *
     * @param map key: part. value: etag
     */
    override fun multipartMerge(uploadId: String?, map: Map<Part, String?>?) {
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
