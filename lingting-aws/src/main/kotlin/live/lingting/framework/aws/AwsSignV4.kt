package live.lingting.framework.aws


import live.lingting.framework.crypto.mac.Mac
import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.HttpRequest
import live.lingting.framework.http.body.BodySource
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.time.DateTime
import live.lingting.framework.util.DigestUtils
import live.lingting.framework.util.StringUtils.deleteLast
import live.lingting.framework.value.multi.StringMultiValue
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.function.BiConsumer

/**
 * @author lingting 2024-09-19 17:01
 */
open class AwsSignV4(
    val dateTime: LocalDateTime,
    val method: String,
    val path: String,
    val headers: HttpHeaders,
    val bodySha256: String,
    val params: StringMultiValue?,
    val region: String,
    val ak: String,
    val sk: String,
    val bucket: String,
    val service: String,
) {

    companion object {

        @JvmField
        val DATETIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")

        @JvmField
        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

        @JvmField
        protected val HEADER_INCLUDE: Array<String> = arrayOf("host", "content-md5", "range")

        const val ALGORITHM: String = "AWS4-HMAC-SHA256"

        const val SCOPE_SUFFIX: String = "aws4_request"

        @JvmStatic
        fun builder(): Builder {
            return Builder()
        }
    }

    fun date(): String {
        return AwsUtils.format(dateTime, DATE_FORMATTER)
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

    fun headersForEach(consumer: BiConsumer<String, Collection<String>>) {
        headers.forEachSorted { k, vs ->
            if (!k.startsWith(AwsUtils.HEADER_PREFIX) && !HEADER_INCLUDE.contains(k)) {
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

    fun scope(date: String): String {
        return "$date/$region/$service/$SCOPE_SUFFIX"
    }

    fun source(date: String, scope: String, request: String): String {
        val requestSha = DigestUtils.sha256Hex(request)
        return "$ALGORITHM\n$date\n$scope\n$requestSha"
    }

    fun sourceHmacSha(source: String, scopeDate: String): String {
        val mac = Mac.hmacBuilder().sha256().secret("AWS4$sk").build()
        val sourceKey1 = mac.calculate(scopeDate)
        val sourceKey2 = mac.useSecret(sourceKey1).calculate(region)
        val sourceKey3 = mac.useSecret(sourceKey2).calculate(service)
        val sourceKey4 = mac.useSecret(sourceKey3).calculate(SCOPE_SUFFIX)

        return mac.useSecret(sourceKey4).calculateHex(source)
    }

    /**
     * 计算签名
     */
    fun calculate(): String {
        val request = canonicalRequest()

        val scopeDate = date()
        val scope = scope(scopeDate)

        val date = headers.first(AwsUtils.HEADER_DATE)!!
        val source = source(date, scope, request)
        val sourceHmacSha = sourceHmacSha(source, scopeDate)

        val signedHeaders = signedHeaders()
        return ("$ALGORITHM Credential=$ak/$scope,SignedHeaders=$signedHeaders,Signature=$sourceHmacSha")
    }

    class Builder internal constructor() {
        private var dateTime: LocalDateTime? = null

        private var method: String? = null

        private var path: String? = null

        private var headers: HttpHeaders? = null

        private var bodySha256: String? = null

        private var params: StringMultiValue? = null

        private var region: String? = null

        private var ak: String? = null

        private var sk: String? = null

        private var bucket: String? = null

        private var service: String? = null

        fun dateTime(dateTime: LocalDateTime): Builder {
            this.dateTime = dateTime
            return this
        }

        fun method(method: HttpMethod): Builder {
            return method(method.name)
        }

        fun method(method: String): Builder {
            this.method = method.uppercase()
            return this
        }

        fun path(path: String): Builder {
            this.path = path
            return this
        }

        fun headers(headers: HttpHeaders): Builder {
            this.headers = headers
            return this
        }

        fun bodyUnsigned(): Builder {
            return body(AwsUtils.PAYLOAD_UNSIGNED)
        }

        fun body(body: HttpRequest.Body): Builder {
            return body(body.string())
        }

        fun body(body: BodySource): Builder {
            return body(body.string())
        }

        fun body(body: String): Builder {
            if (AwsUtils.PAYLOAD_UNSIGNED == body) {
                return bodySha256(AwsUtils.PAYLOAD_UNSIGNED)
            }
            val hex = DigestUtils.sha256Hex(body)
            return bodySha256(hex)
        }

        fun bodySha256(bodySha256: String): Builder {
            this.bodySha256 = bodySha256
            return this
        }

        fun params(params: StringMultiValue): Builder {
            this.params = params
            return this
        }

        fun region(region: String): Builder {
            this.region = region
            return this
        }

        fun ak(ak: String): Builder {
            this.ak = ak
            return this
        }

        fun sk(sk: String): Builder {
            this.sk = sk
            return this
        }

        fun bucket(bucket: String): Builder {
            this.bucket = bucket
            return this
        }

        fun service(service: String): Builder {
            this.service = service
            return this
        }

        fun build(): AwsSignV4 {
            val time = dateTime ?: DateTime.current()
            return AwsSignV4(
                time, method!!, path!!, headers!!, bodySha256!!, params,
                region!!, ak!!, sk!!, bucket!!, service!!
            )
        }
    }

}
