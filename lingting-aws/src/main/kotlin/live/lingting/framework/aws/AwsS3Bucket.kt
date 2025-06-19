package live.lingting.framework.aws

import live.lingting.framework.aws.properties.S3Properties
import live.lingting.framework.aws.s3.interfaces.AwsS3BucketInterface
import live.lingting.framework.aws.s3.request.AwsS3ListObjectRequest
import live.lingting.framework.aws.s3.request.AwsS3SimpleRequest
import live.lingting.framework.aws.s3.response.AwsS3ListObjectResponse
import live.lingting.framework.aws.s3.response.AwsS3MultipartItem
import live.lingting.framework.http.HttpMethod
import live.lingting.framework.jackson.xml.JacksonXmlUtils
import java.util.function.Consumer

/**
 * @author lingting 2024-09-19 15:09
 */
class AwsS3Bucket(properties: S3Properties) : AwsS3Client(properties), AwsS3BucketInterface {
    override fun use(key: String): AwsS3Object {
        return AwsS3Object(properties, key)
    }

    /**
     * 列举所有未完成的分片上传
     * @return k: uploadId, v: k
     */
    override fun multipartList(): List<AwsS3MultipartItem> {
        return multipartList(null)
    }

    override fun multipartList(consumer: Consumer<AwsS3SimpleRequest>?): List<AwsS3MultipartItem> {
        val request = AwsS3SimpleRequest(HttpMethod.GET)
        consumer?.accept(request)
        request.params.add("uploads")
        val response = call(request)
        val xml = response.string()
        val list = ArrayList<AwsS3MultipartItem>()
        try {
            val node = JacksonXmlUtils.toNode(xml)
            val tree = node["Upload"] ?: return list
            if (tree.isArray && !tree.isEmpty) {
                tree.forEach(Consumer {
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
        return list
    }

    override fun listObjects(request: AwsS3ListObjectRequest): AwsS3ListObjectResponse {
        val response = call(request)
        val xml = response.string()
        val r = AwsS3ListObjectResponse(request)
        val node = JacksonXmlUtils.toNode(xml)
        r.name = node["Name"]?.asText() ?: ""
        r.maxKeys = request.maxKeys
        r.encodeType = node["EncodingType"]?.asText()
        r.isTruncated = node["IsTruncated"].asBoolean()
        r.prefix = node["Prefix"]?.asText()
        r.delimiter = node["Delimiter"]?.asText()

        if (request.v2) {
            r.nextToken = node["NextContinuationToken"]?.asText()
        } else {
            r.nextToken = node["NextMarker"]?.asText()
        }

        r.commonPrefixes = node["CommonPrefixes"]?.let {
            if (it.isArray) {
                val list = ArrayList<String>()
                if (!it.isEmpty) {
                    it.forEach(Consumer {
                        list.add(it["Prefix"].asText())
                    })
                }
                list
            } else {
                listOf(it.asText())
            }
        }

        r.contents = node["Contents"]?.let {
            if (it.isArray) {
                val list = ArrayList<AwsS3ListObjectResponse.Content>()
                if (!it.isEmpty) {
                    it.forEach(Consumer {
                        list.add(AwsS3ListObjectResponse.Content.of(it))
                    })
                }
                list
            } else {
                listOf(AwsS3ListObjectResponse.Content.of(it))
            }
        }

        r.keyCount = r.contents?.size ?: 0
        return r
    }

}
