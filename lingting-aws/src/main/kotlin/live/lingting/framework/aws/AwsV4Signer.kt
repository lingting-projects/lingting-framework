package live.lingting.framework.aws

import live.lingting.framework.crypto.mac.Mac
import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.api.ApiRequest
import live.lingting.framework.http.body.Body
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.util.DigestUtils
import live.lingting.framework.util.StringUtils.deleteLast
import live.lingting.framework.value.multi.StringMultiValue
import java.time.Duration
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
    val body: Body?,
    params: StringMultiValue,
    val region: String,
    ak: String,
    val sk: String,
    val service: String,
) : AwsSigner<AwsV4Signer, AwsV4Signer.Signed>(ak) {

    constructor(
        request: ApiRequest,
        region: String,
        ak: String,
        sk: String,
        service: String
    ) : this(request.method(), request.path(), request.headers, request.body(), request.params, region, ak, sk, service)

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

    open val headerDate by lazy { "$headerPrefix-date" }

    open val headerContentPayload by lazy { "$headerPrefix-content-sha256" }

    open val headerSecurityToken by lazy { "$headerPrefix-security-token" }

    open val headers = HttpHeaders.empty().also { it.putAll(headers) }

    open val params = StringMultiValue().also { it.putAll(params) }

    open val path = path.let {
        if (path.startsWith("/")) path else "/$path"
    }

    open val bodyPayload by lazy {
        body.let {
            if (it == null || it.length() < 1) {
                AwsUtils.PAYLOAD_UNSIGNED
            } else {
                DigestUtils.sha256Hex(it.string())
            }
        }
    }

    open fun date(time: LocalDateTime) = AwsUtils.format(time, dateFormatter)

    open fun canonicalUri() = path

    open fun canonicalQuery(): String {
        val builder = StringBuilder()
        if (!params.isEmpty) {
            params.forEachSorted { k, vs ->
                val name: String = HttpUrlBuilder.encode(k)
                if (vs.isEmpty()) {
                    builder.append(name).append("=").append("&")
                    return@forEachSorted
                }
                vs.sorted().forEach { v ->
                    val value: String = HttpUrlBuilder.encode(v)
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

    override fun signed(time: LocalDateTime): Signed = signed(time, bodyPayload)

    override fun signed(
        time: LocalDateTime,
        bodyPayload: String
    ): Signed {
        headers.put(headerContentPayload, bodyPayload)

        val date = date(time)
        headers.put(headerDate, date)

        val scopeDate = scopeDate(time)
        val scope = scope(scopeDate)

        val canonicalUri = canonicalUri()
        val canonicalQuery = canonicalQuery()
        val canonicalHeaders = canonicalHeaders()
        val signedHeaders = signedHeaders()

        val canonicalRequest =
            canonicalRequest(canonicalUri, canonicalQuery, canonicalHeaders, signedHeaders, bodyPayload)

        val source = source(canonicalRequest, date, scope)

        val calculate = calculate(scopeDate, source)

        val authorization = authorization(scope, signedHeaders, calculate)

        return Signed(
            this,
            headers,
            params,
            bodyPayload,
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

    override fun signed(
        time: LocalDateTime,
        expire: LocalDateTime
    ): Signed = signed(time, expire, bodyPayload)


    override fun signed(
        time: LocalDateTime,
        duration: Duration
    ): Signed = signed(time, duration, bodyPayload)

    override fun signed(
        time: LocalDateTime,
        duration: Duration,
        bodyPayload: String
    ): Signed {
        val date = date(time)
        val token = headers.first(headerSecurityToken)
        if (!token.isNullOrEmpty()) {
            params.add(AwsUtils.toParamsKey(headerSecurityToken), token)
        }
        params.add(AwsUtils.toParamsKey(headerDate), date)
        params.add(AwsUtils.toParamsKey("$headerPrefix-Expires"), duration.seconds.toString())
        params.add(AwsUtils.toParamsKey("$headerPrefix-Algorithm"), algorithm)

        val scopeDate = scopeDate(time)
        val scope = scope(scopeDate)

        params.add(AwsUtils.toParamsKey("$headerPrefix-Credential"), "$ak/$scope")
        val signedHeaders = signedHeaders()

        params.add(AwsUtils.toParamsKey("$headerPrefix-SignedHeaders"), signedHeaders)

        val canonicalUri = canonicalUri()
        val canonicalQuery = canonicalQuery()
        val canonicalHeaders = canonicalHeaders()

        val canonicalRequest =
            canonicalRequest(canonicalUri, canonicalQuery, canonicalHeaders, signedHeaders, bodyPayload)

        val source = source(canonicalRequest, date, scope)

        val calculate = calculate(scopeDate, source)

        params.add(AwsUtils.toParamsKey("$headerPrefix-Signature"), calculate)

        return Signed(
            this,
            headers,
            params,
            bodyPayload,
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
            "",
        )
    }

    open class Signed(
        signer: AwsV4Signer,
        headers: HttpHeaders,
        params: StringMultiValue?,
        bodyPayload: String,
        open val canonicalUri: String,
        open val canonicalQuery: String,
        open val canonicalHeaders: String,
        open val signedHeaders: String,
        open val canonicalRequest: String,
        open val date: String,
        open val scopeDate: String,
        open val scope: String,
        source: String,
        sign: String,
        authorization: String,
    ) : AwsSigner.Signed<AwsV4Signer, Signed>(signer, headers, params, bodyPayload, source, sign, authorization)

}
