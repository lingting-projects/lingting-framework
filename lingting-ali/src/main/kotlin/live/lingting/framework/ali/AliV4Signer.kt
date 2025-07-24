package live.lingting.framework.ali

import live.lingting.framework.aws.AwsUtils.HEADER_MD5
import live.lingting.framework.aws.AwsV4Signer
import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.QueryBuilder
import live.lingting.framework.http.body.Body
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.value.multi.StringMultiValue

/**
 * @author lingting 2025/6/4 17:37
 */
class AliV4Signer(
    method: HttpMethod,
    path: String,
    headers: HttpHeaders,
    body: Body?,
    params: StringMultiValue,
    region: String,
    ak: String,
    sk: String,
    service: String,
) : AwsV4Signer(method, path, headers, body, params, region, ak, sk, service) {

    override val algorithm: String = "OSS4-HMAC-SHA256"

    override val secretPrefix: String = "aliyun_v4"

    override val scopeSuffix: String = "aliyun_v4_request"

    override val nameSignedHeaders: String = "AdditionalHeaders"

    override val nameAlgorithm: String = "signature-version"

    override val headerPrefix: String = AliUtils.HEADER_PREFIX

    override val headerInclude: Array<String> = arrayOf("content-type", HEADER_MD5)

    override fun toParamName(key: String): String {
        return key.lowercase()
    }

    override fun addParam(key: String, value: String) {
        if (key == nameSignedHeaders && value.isBlank()) {
            return
        }
        super.addParam(key, value)
    }

    override fun canonicalQuery(): String {
        return QueryBuilder(params).apply {
            sort = true
        }.build()
    }

    /**
     * 应为 AdditionalHeaders
     */
    override fun signedHeaders(): String {
        return ""
    }

    override fun authorization(scope: String, signedHeaders: String, sign: String): String {
        return buildString {
            append(algorithm)
            append(" Credential=")
            append(ak)
            append("/")
            append(scope)
            if (signedHeaders.isNotBlank()) {
                append(", ")
                append(nameSignedHeaders)
                append("=")
                append(signedHeaders)
            }
            append(", Signature=")
            append(sign)
        }
    }

}
