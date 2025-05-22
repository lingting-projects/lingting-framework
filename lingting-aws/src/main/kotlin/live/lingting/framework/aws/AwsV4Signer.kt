package live.lingting.framework.aws

import live.lingting.framework.crypto.mac.Mac
import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.api.ApiRequest
import live.lingting.framework.http.body.BodySource
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.util.DigestUtils
import live.lingting.framework.util.StringUtils.deleteLast
import live.lingting.framework.value.multi.StringMultiValue
import java.time.LocalDateTime
import java.util.function.BiConsumer

/**
 * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/sig-v4-authenticating-requests.html">签名文档</a>
 * @author lingting 2025/5/21 19:40
 */
open class AwsV4Signer(
    val method: HttpMethod,
    path: String,
    headers: HttpHeaders,
    val body: BodySource?,
    val params: StringMultiValue?,
    val region: String,
    val ak: String,
    val sk: String,
    val service: String,
) : AwsSigner() {

    /**
     * 用于类似算法直接使用时设置
     */
    open val dateFormatter = AwsUtils.DATE_FORMATTER

    open val scopeDateFormatter = AwsUtils.SCOPE_DATE_FORMATTER

    open val algorithm = "AWS4-HMAC-SHA256"

    open val secretPrefix = "AWS4"

    open val scopeSuffix = "aws4_request"

    open val headerPrefix = AwsUtils.HEADER_PREFIX

    /**
     * 除指定前缀开头的请求外, 其他参与签名的头
     */
    open val headerInclude = arrayOf("host", "content-md5", "range")

    open val headerDate = "$headerPrefix-date"

    open val headerContentPayload = "$headerPrefix-content-sha256"

    open val headers = HttpHeaders.empty().also { it.putAll(headers) }

    open val path = path.let {
        if (path.startsWith("/")) path else "/$path"
    }

    open val bodyPayload = body.let {
        if (it == null || it.length() < 1) {
            AwsUtils.PAYLOAD_UNSIGNED
        } else {
            DigestUtils.sha256Hex(it.string())
        }
    }

    constructor(
        request: ApiRequest,
        region: String,
        ak: String,
        sk: String,
        service: String
    ) : this(request.method(), request.path(), request.headers, request.body(), request.params, region, ak, sk, service)

    open fun date(time: LocalDateTime) = AwsUtils.format(time, dateFormatter)

    open fun canonicalUri() = path

    open fun canonicalQuery(): String {
        val builder = StringBuilder()
        if (params != null && !params.isEmpty) {
            params.forEachSorted { k, vs ->
                val name: String = AwsUtils.encode(k)
                if (vs.isEmpty()) {
                    builder.append(name).append("=").append("&")
                    return@forEachSorted
                }
                vs.sorted().forEach { v ->
                    val value: String = AwsUtils.encode(v)
                    builder.append(name).append("=").append(value).append("&")
                }
            }
        }
        return deleteLast(builder).toString()
    }

    open fun headersForEach(consumer: BiConsumer<String, Collection<String>>) {
        headers.forEachSorted { k, vs ->
            if (!k.startsWith(headerPrefix) && !headerInclude.contains(k)) {
                return@forEachSorted
            }
            consumer.accept(k, vs)
        }
    }

    open fun canonicalHeaders(): String {
        val builder = StringBuilder()

        headersForEach { k, vs ->
            for (v in vs) {
                builder.append(k).append(":").append(v.trim()).append("\n")
            }
        }

        return builder.toString()
    }

    open fun signedHeaders(): String {
        val builder = StringBuilder()

        headersForEach { k, _ -> builder.append(k).append(";") }

        return deleteLast(builder).toString()
    }

    open fun canonicalRequest(): String {
        val canonicalUri = canonicalUri()
        val canonicalQuery = canonicalQuery()
        val canonicalHeaders = canonicalHeaders()
        val signedHeaders = signedHeaders()

        return canonicalRequest(canonicalUri, canonicalQuery, canonicalHeaders, signedHeaders)
    }

    @JvmOverloads
    open fun canonicalRequest(
        canonical: String,
        canonicalQuery: String,
        canonicalHeaders: String,
        signedHeaders: String,
        bodyPayload: String = this.bodyPayload,
    ): String {
        return "$method\n$canonical\n$canonicalQuery\n$canonicalHeaders\n$signedHeaders\n$bodyPayload"
    }

    open fun scopeDate(time: LocalDateTime) = AwsUtils.format(time, scopeDateFormatter)

    open fun scope(time: LocalDateTime) = scope(scopeDate(time))

    open fun scope(scopeDate: String): String {
        return "$scopeDate/$region/$service/$scopeSuffix"
    }

    /**
     * 签名源
     */
    open fun source(canonicalRequest: String, scopeDate: String, scope: String): String {
        val requestSha = DigestUtils.sha256Hex(canonicalRequest)
        return "$algorithm\n$scopeDate\n$scope\n$requestSha"
    }

    /**
     * 计算签名
     */
    open fun calculate(time: LocalDateTime): String {
        val request = canonicalRequest()
        val scopeDate = scopeDate(time)
        val scope = scope(scopeDate)
        return calculate(request, scopeDate, scope)
    }

    open fun calculate(canonicalRequest: String, scopeDate: String, scope: String): String {
        val source = source(canonicalRequest, scopeDate, scope)
        return calculate(scopeDate, source)
    }

    open fun calculate(scopeDate: String, source: String): String {
        val mac = Mac.hmacBuilder().sha256().secret("$secretPrefix$sk").build()
        val sourceKey1 = mac.calculate(scopeDate)
        val sourceKey2 = mac.useSecret(sourceKey1).calculate(region)
        val sourceKey3 = mac.useSecret(sourceKey2).calculate(service)
        val sourceKey4 = mac.useSecret(sourceKey3).calculate(scopeSuffix)

        return mac.useSecret(sourceKey4).calculateHex(source)
    }

    open fun authorization(scope: String, sign: String): String {
        val signedHeaders = signedHeaders()
        return authorization(scope, signedHeaders, sign)
    }

    open fun authorization(scope: String, signedHeaders: String, sign: String): String {
        return "$algorithm Credential=$ak/$scope,SignedHeaders=$signedHeaders,Signature=$sign"
    }

    override fun signed(time: LocalDateTime): Signed {
        return signed(time, bodyPayload)
    }

    override fun signed(time: LocalDateTime, bodyPayload: String): Signed {
        headers.put(headerContentPayload, bodyPayload)

        val date = date(time)
        headers.put(headerDate, date)

        val canonicalUri = canonicalUri()
        val canonicalQuery = canonicalQuery()
        val canonicalHeaders = canonicalHeaders()
        val signedHeaders = signedHeaders()

        val canonicalRequest =
            canonicalRequest(canonicalUri, canonicalQuery, canonicalHeaders, signedHeaders, bodyPayload)

        val scopeDate = scopeDate(time)
        val scope = scope(scopeDate)
        val source = source(canonicalRequest, date, scope)

        val calculate = calculate(scopeDate, source)

        val authorization = authorization(scope, signedHeaders, calculate)
        return Signed(
            this,
            headers,
            canonicalUri,
            canonicalQuery,
            canonicalHeaders,
            signedHeaders,
            canonicalRequest,
            date,
            scopeDate,
            scope,
            source,
            calculate,
            authorization,
        )
    }

    open class Signed(
        override val signer: AwsV4Signer,
        override val headers: HttpHeaders,
        open val canonicalUri: String,
        open val canonicalQuery: String,
        open val canonicalHeaders: String,
        open val signedHeaders: String,
        open val canonicalRequest: String,
        open val date: String,
        open val scopeDate: String,
        open val scope: String,
        override val source: String,
        override val sign: String,
        override val authorization: String,
    ) : AwsSigner.Signed<AwsV4Signer>(signer, headers, source, sign, authorization)

}
