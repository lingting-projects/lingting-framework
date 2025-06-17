package live.lingting.framework.ali

import live.lingting.framework.aws.AwsSigner
import live.lingting.framework.aws.AwsUtils
import live.lingting.framework.crypto.mac.Mac
import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.body.Body
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.time.DatePattern
import live.lingting.framework.util.ArrayUtils.contains
import live.lingting.framework.util.DigestUtils
import live.lingting.framework.util.StringUtils.deleteLast
import live.lingting.framework.value.multi.StringMultiValue
import java.time.Duration
import java.time.LocalDateTime
import java.util.function.BiConsumer

/**
 * @author lingting 2025/5/27 20:17
 */
class AliV3Signer(
    val method: HttpMethod,
    path: String,
    headers: HttpHeaders,
    val body: Body?,
    params: StringMultiValue,
    ak: String,
    val sk: String,
) : AwsSigner<AliV3Signer, AliV3Signer.Signed>(ak) {

    companion object {

        @JvmField
        val ALGORITHM: String = "ACS3-HMAC-SHA256"

        @JvmField
        val HEADER_PREFIX: String = "x-acs"

        @JvmField
        val HEADER_INCLUDE = arrayOf("host", "content-type", "content-md5")

    }

    val headers = HttpHeaders.empty().also { it.putAll(headers) }

    val params = StringMultiValue().also { it.putAll(params) }

    val path = path.let {
        if (path.startsWith("/")) path else "/$path"
    }

    override val bodyPayload by lazy {
        body.let {
            if (it == null || it.length() < 1) {
                ""
            } else {
                DigestUtils.sha256Hex(it.string())
            }
        }
    }

    fun date(time: LocalDateTime) = AwsUtils.format(time, DatePattern.FORMATTER_ISO_8601)

    fun canonicalUri() = path

    fun canonicalQuery(): String {
        return HttpUrlBuilder.buildQuery(params, sort = true)
    }

    fun headersForEach(consumer: BiConsumer<String, Collection<String>>) {
        headers.forEachSorted { k, vs ->
            if (!k.startsWith(HEADER_PREFIX) && !HEADER_INCLUDE.contains(k)) {
                return@forEachSorted
            }
            consumer.accept(k, vs)
        }
    }

    fun canonicalHeaders(): String {
        val builder = StringBuilder()

        headersForEach { k, vs ->
            for (v in vs) {
                builder.append(k).append(":").append(v).append("\n")
            }
        }

        return builder.toString()
    }

    fun signedHeaders(): String {
        val builder = StringBuilder()

        headersForEach { k, _ -> builder.append(k).append(";") }

        return deleteLast(builder).toString()
    }

    fun canonicalRequest(): String {
        val uri = canonicalUri()
        val query = canonicalQuery()
        val canonicalHeaders = canonicalHeaders()
        val signedHeaders = signedHeaders()

        return canonicalRequest(uri, query, canonicalHeaders, signedHeaders)
    }

    @JvmOverloads
    fun canonicalRequest(
        uri: String,
        query: String,
        canonicalHeaders: String,
        signedHeaders: String,
        bodyPayload: String = this.bodyPayload,
    ): String {
        return "$method\n$uri\n$query\n$canonicalHeaders\n$signedHeaders\n$bodyPayload"
    }

    fun source(request: String): String {
        val requestSha = DigestUtils.sha256Hex(request)
        return "${ALGORITHM}\n$requestSha"
    }

    fun calculate(source: String): String {
        val mac = Mac.hmacBuilder().sha256().secret(sk).build()
        return mac.calculateHex(source)
    }

    fun authorization(signedHeaders: String, sign: String): String {
        return "$ALGORITHM Credential=$ak,SignedHeaders=$signedHeaders,Signature=$sign"
    }

    override fun signed(time: LocalDateTime): Signed = signed(time, bodyPayload)

    override fun signed(
        time: LocalDateTime,
        bodyPayload: String
    ): Signed {
        val date = date(time)
        headers.put("$HEADER_PREFIX-date", date)
        headers.put("$HEADER_PREFIX-content-sha256", bodyPayload)


        val canonicalUri = canonicalUri()
        val canonicalQuery = canonicalQuery()
        val canonicalHeaders = canonicalHeaders()
        val signedHeaders = signedHeaders()

        val canonicalRequest =
            canonicalRequest(canonicalUri, canonicalQuery, canonicalHeaders, signedHeaders, bodyPayload)

        val source = source(canonicalRequest)

        val calculate = calculate(source)

        val authorization = authorization(signedHeaders, calculate)

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
            source,
            calculate,
            authorization,
        )
    }

    override fun signed(
        time: LocalDateTime,
        duration: Duration,
        bodyPayload: String,
    ): Signed {
        throw UnsupportedOperationException()
    }

    class Signed(
        signer: AliV3Signer,
        headers: HttpHeaders,
        params: StringMultiValue?,
        bodyPayload: String,
        val canonicalUri: String,
        val canonicalQuery: String,
        val canonicalizedHeaders: String,
        val signedHeaders: String,
        val canonicalRequest: String,
        val date: String,
        source: String,
        sign: String,
        authorization: String
    ) : AwsSigner.Signed<AliV3Signer, Signed>(
        signer,
        headers,
        params,
        bodyPayload,
        source,
        sign,
        authorization,
    ) {

    }

}
