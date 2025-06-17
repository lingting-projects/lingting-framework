package live.lingting.framework.aws.s3.response

import com.fasterxml.jackson.databind.JsonNode
import live.lingting.framework.aws.s3.enums.StorageClass
import live.lingting.framework.aws.s3.request.AwsS3ListObjectRequest

/**
 * @author lingting 2025/1/14 20:07
 */
class AwsS3ListObjectResponse(private val request: AwsS3ListObjectRequest) {

    var name: String = ""
    var maxKeys: Int = 0
    var encodeType: String? = null
    var keyCount: Int = 0

    /**
     * 是否还有数据
     */
    var isTruncated: Boolean = false
    var prefix: String? = null
    var nextToken: String? = null
    var delimiter: String? = null
    var commonPrefixes: List<String>? = null
    var contents: List<Content>? = null

    class Content {

        companion object {

            @JvmStatic
            fun of(node: JsonNode): Content {
                val content = Content()
                content.key = node["Key"].asText()
                content.lastModified = node["LastModified"].asText()
                content.eTag = node["ETag"].asText()
                val ownerNode = node["Owner"]
                if (ownerNode != null && !ownerNode.isEmpty) {
                    content.owner = Owner().also {
                        it.displayName = ownerNode["DisplayName"]?.asText() ?: ""
                        it.id = ownerNode["ID"].asText()
                    }
                }
                content.storageClass = StorageClass.of(node["StorageClass"]?.asText())
                content.size = node["Size"].asLong()
                return content
            }
        }

        var key: String = ""
        var lastModified: String = ""
        var eTag: String = ""
        var owner: Owner? = null
        var storageClass: StorageClass? = null
        var size: Long = 0
    }

    class Owner {
        var displayName: String = ""
        var id: String = ""
    }

    fun nextRequest(): AwsS3ListObjectRequest? {
        if (!isTruncated) {
            return null
        }
        return request.copy().also {
            it.token = nextToken
        }
    }

}
