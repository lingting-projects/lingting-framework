package live.lingting.framework.aws

import com.fasterxml.jackson.databind.JsonNode
import live.lingting.framework.aws.s3.AwsS3Properties
import live.lingting.framework.aws.s3.interfaces.AwsS3BucketInterface
import live.lingting.framework.aws.s3.request.AwsS3SimpleRequest
import live.lingting.framework.aws.s3.response.AwsS3MultipartItem
import live.lingting.framework.http.HttpMethod
import live.lingting.framework.jackson.JacksonUtils
import java.util.function.Consumer

/**
 * @author lingting 2024-09-19 15:09
 */
class AwsS3Bucket(properties: AwsS3Properties) : AwsS3Client(properties), AwsS3BucketInterface {
    override fun use(key: String?): AwsS3Object {
        return AwsS3Object(properties, key)
    }

    /**
     * 列举所有未完成的分片上传
     * @return k: uploadId, v: k
     */
    override fun multipartList(): List<AwsS3MultipartItem?>? {
        return multipartList(null)
    }

    override fun multipartList(consumer: Consumer<AwsS3SimpleRequest?>?): List<AwsS3MultipartItem?>? {
        val request = AwsS3SimpleRequest(HttpMethod.GET)
        consumer?.accept(request)
        request.params.add("uploads")
        val response = call(request)
        return response.convert<List<AwsS3MultipartItem?>> { xml: String? ->
            val list: MutableList<AwsS3MultipartItem?> = ArrayList()
            try {
                val node = JacksonUtils.xmlToNode(xml)
                val tree = node["Upload"] ?: return@convert list
                if (tree.isArray && !tree.isEmpty) {
                    tree.forEach(Consumer { it: JsonNode ->
                        val key = it["Key"].asText()
                        val uploadId = it["UploadId"].asText()
                        list.add(AwsS3MultipartItem(key, uploadId))
                    })
                } else if (tree.isObject) {
                    val key = tree["Key"].asText()
                    val uploadId = tree["UploadId"].asText()
                    list.add(AwsS3MultipartItem(key, uploadId))
                }
            } catch (e: Exception) {
                log.warn("AliOssBucket multipartList error!", e)
            }
            list
        }
    }
}
