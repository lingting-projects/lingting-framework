package live.lingting.framework.ali.sts

import live.lingting.framework.ali.AliClient
import live.lingting.framework.ali.AliSts
import live.lingting.framework.crypto.mac.Mac
import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.HttpRequest
import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.body.BodySource
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.util.ArrayUtils.containsIgnoreCase
import live.lingting.framework.util.DigestUtils
import live.lingting.framework.util.StringUtils.deleteLast
import live.lingting.framework.value.multi.StringMultiValue
import java.util.function.BiConsumer

/**
 * @author lingting 2025/4/22 19:44
 */
class AliStsSing(
    val method: String,
    val path: String,
    val headers: HttpHeaders,
    val bodySha256: String,
    val params: StringMultiValue,
    val ak: String,
    val sk: String,
) {

    companion object {

        @JvmStatic
        fun builder() = AliStsSingBuilder()

    }

    fun canonicalUri(): String {
        val builder = StringBuilder()
        if (!path.startsWith("/")) {
            builder.append("/")
        }
        builder.append(path)

        return builder.toString()
    }

    fun canonicalQuery(): String {
        return HttpUrlBuilder.buildQuery(params)
    }

    fun headersForEach(consumer: BiConsumer<String, Collection<String>>) {
        headers.forEachSorted { k, vs ->
            if (!k.startsWith(AliSts.HEADER_PREFIX) && !AliClient.HEADER_INCLUDE.containsIgnoreCase(k)) {
                return@forEachSorted
            }
            consumer.accept(k, vs)
        }
    }

    fun canonicalHeaders(): String {
        val builder = StringBuilder()

        headersForEach { k, vs ->
            for (v in vs) {
                builder.append(k).append(":").append(v.trim()).append("\n")
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

    fun canonicalRequest(uri: String, query: String, canonicalHeaders: String, signedHeaders: String): String {
        return "$method\n$uri\n$query\n$canonicalHeaders\n$signedHeaders\n$bodySha256"
    }

    fun source(request: String): String {
        val requestSha = DigestUtils.sha256Hex(request)
        return "${AliSts.ALGORITHM}\n$requestSha"
    }

    fun sourceHmacSha(source: String): String {
        val mac = Mac.hmacBuilder().sha256().secret(sk).build()
        return mac.calculateHex(source)
    }

    /**
     * 计算签名
     */
    fun calculate(): String {
        val request = canonicalRequest()
        val source = source(request)
        val sourceHmacSha = sourceHmacSha(source)

        val signedHeaders = signedHeaders()
        return "${AliSts.ALGORITHM} Credential=$ak,SignedHeaders=$signedHeaders,Signature=$sourceHmacSha"
    }

    class AliStsSingBuilder internal constructor() {

        private var method: String? = null

        private var path: String? = null

        private var headers: HttpHeaders? = null

        private var bodySha256: String? = null

        private var params: StringMultiValue? = null

        private var ak: String? = null

        private var sk: String? = null

        fun method(method: HttpMethod): AliStsSingBuilder {
            return method(method.name)
        }

        fun method(method: String): AliStsSingBuilder {
            this.method = method.uppercase()
            return this
        }

        fun path(path: String): AliStsSingBuilder {
            this.path = path
            return this
        }

        fun headers(headers: HttpHeaders): AliStsSingBuilder {
            this.headers = headers
            return this
        }

        fun body(body: HttpRequest.Body): AliStsSingBuilder {
            return body(body.string())
        }

        fun body(body: BodySource): AliStsSingBuilder {
            return body(body.string())
        }

        fun body(body: String): AliStsSingBuilder {
            val hex = DigestUtils.sha256Hex(body)
            return bodySha256(hex)
        }

        fun bodySha256(bodySha256: String): AliStsSingBuilder {
            this.bodySha256 = bodySha256
            return this
        }

        fun params(params: StringMultiValue): AliStsSingBuilder {
            this.params = params
            return this
        }

        fun ak(ak: String): AliStsSingBuilder {
            this.ak = ak
            return this
        }

        fun sk(sk: String): AliStsSingBuilder {
            this.sk = sk
            return this
        }

        fun build(): AliStsSing {
            return AliStsSing(
                this.method!!,
                this.path!!,
                this.headers!!,
                this.bodySha256!!,
                this.params ?: StringMultiValue(),
                this.ak!!,
                this.sk!!,
            )
        }
    }
}
