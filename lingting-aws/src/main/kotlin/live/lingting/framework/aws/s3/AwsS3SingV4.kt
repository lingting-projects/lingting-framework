package live.lingting.framework.aws.s3

import live.lingting.framework.crypto.mac.Mac
import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.HttpRequest
import live.lingting.framework.http.body.BodySource
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.util.ArrayUtils
import live.lingting.framework.util.CollectionUtils
import live.lingting.framework.util.DigestUtils
import live.lingting.framework.util.StringUtils
import live.lingting.framework.value.multi.StringMultiValue
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.function.BiConsumer

/**
 * @author lingting 2024-09-19 17:01
 */
class AwsS3SingV4(protected val dateTime: LocalDateTime, protected val method: String?, protected val path: String?, @JvmField val headers: HttpHeaders?, protected val bodySha256: String?, protected val params: StringMultiValue?, protected val region: String?, protected val ak: String?, protected val sk: String?, protected val bucket: String?) {
    class S3SingV4Builder internal constructor() {
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

        fun dateTime(dateTime: LocalDateTime?): S3SingV4Builder {
            this.dateTime = dateTime
            return this
        }

        fun method(method: HttpMethod): S3SingV4Builder {
            return method(method.name)
        }

        fun method(method: String): S3SingV4Builder {
            this.method = method.uppercase(Locale.getDefault())
            return this
        }

        fun path(path: String?): S3SingV4Builder {
            this.path = path
            return this
        }

        fun headers(headers: HttpHeaders?): S3SingV4Builder {
            this.headers = headers
            return this
        }

        @Throws(NoSuchAlgorithmException::class)
        fun bodyUnsigned(): S3SingV4Builder {
            return body(AwsS3Utils.Companion.PAYLOAD_UNSIGNED)
        }

        @Throws(NoSuchAlgorithmException::class)
        fun body(body: HttpRequest.Body): S3SingV4Builder {
            return body(body.string())
        }

        @Throws(NoSuchAlgorithmException::class)
        fun body(body: BodySource): S3SingV4Builder {
            return body(body.string())
        }

        @Throws(NoSuchAlgorithmException::class)
        fun body(body: String?): S3SingV4Builder {
            if (AwsS3Utils.Companion.PAYLOAD_UNSIGNED == body) {
                return bodySha256(AwsS3Utils.Companion.PAYLOAD_UNSIGNED)
            }
            val hex = DigestUtils.sha256Hex(body!!)
            return bodySha256(hex)
        }

        fun bodySha256(bodySha256: String?): S3SingV4Builder {
            this.bodySha256 = bodySha256
            return this
        }

        fun params(params: StringMultiValue?): S3SingV4Builder {
            this.params = params
            return this
        }

        fun region(region: String?): S3SingV4Builder {
            this.region = region
            return this
        }

        fun ak(ak: String?): S3SingV4Builder {
            this.ak = ak
            return this
        }

        fun sk(sk: String?): S3SingV4Builder {
            this.sk = sk
            return this
        }

        fun bucket(bucket: String?): S3SingV4Builder {
            this.bucket = bucket
            return this
        }

        fun build(): AwsS3SingV4 {
            val time = if (this.dateTime == null) LocalDateTime.now() else dateTime!!
            return AwsS3SingV4(
                time, this.method, this.path, this.headers, this.bodySha256, this.params,
                this.region, this.ak, this.sk, this.bucket
            )
        }
    }

    fun date(): String {
        return AwsS3Utils.Companion.format(dateTime, DATE_FORMATTER)
    }

    fun canonicalUri(): String {
        val builder = StringBuilder("/")

        if (StringUtils.hasText(path)) {
            builder.append(path, if (path!!.startsWith("/")) 1 else 0, path.length)
        }

        return builder.toString()
    }

    fun canonicalQuery(): String {
        val builder = StringBuilder()
        if (params != null && !params.isEmpty) {
            params.forEachSorted(BiConsumer<String, Collection<String?>> { k: String, vs: Collection<String?> ->
                val name: String = AwsS3Utils.Companion.encode(k)
                if (CollectionUtils.isEmpty(vs)) {
                    builder.append(name).append("=").append("&")
                    return@forEachSorted
                }
                vs.stream().sorted().forEach { v: String ->
                    val value: String = AwsS3Utils.Companion.encode(v)
                    builder.append(name).append("=").append(value).append("&")
                }
            })
        }
        return deleteLast(builder).toString()
    }

    fun headersForEach(consumer: BiConsumer<String?, Collection<String>>) {
        headers!!.forEachSorted(BiConsumer<String, Collection<String>> { k: String, vs: Collection<String> ->
            if (!k.startsWith(AwsS3Utils.Companion.HEADER_PREFIX) && !ArrayUtils.contains(HEADER_INCLUDE, k)) {
                return@forEachSorted
            }
            consumer.accept(k, vs)
        })
    }

    fun canonicalHeaders(): String {
        val builder = StringBuilder()

        headersForEach { k: String?, vs: Collection<String> ->
            for (v in vs) {
                builder.append(k).append(":").append(v.trim { it <= ' ' }).append("\n")
            }
        }

        return builder.toString()
    }

    fun signedHeaders(): String {
        val builder = StringBuilder()

        headersForEach { k: String?, vs: Collection<String>? -> builder.append(k).append(";") }

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
        return """
            $method
            $uri
            $query
            $canonicalHeaders
            $signedHeaders
            $bodySha256
            """.trimIndent()
    }

    fun scope(date: String): String {
        return date + "/" + region + "/s3/" + SCOPE_SUFFIX
    }

    @Throws(NoSuchAlgorithmException::class)
    fun source(date: String?, scope: String, request: String?): String {
        val requestSha = DigestUtils.sha256Hex(request!!)
        return """
            $ALGORITHM
            $date
            $scope
            $requestSha
            """.trimIndent()
    }

    @Throws(InvalidAlgorithmParameterException::class, NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun sourceHmacSha(source: String, scopeDate: String): String {
        val mac = Mac.hmacBuilder().sha256().secret("AWS4$sk").build()
        val sourceKey1 = mac.calculate(scopeDate)
        val sourceKey2 = mac.useSecret(sourceKey1).calculate(region!!)
        val sourceKey3 = mac.useSecret(sourceKey2).calculate("s3")
        val sourceKey4 = mac.useSecret(sourceKey3).calculate(SCOPE_SUFFIX)

        return mac.useSecret(sourceKey4).calculateHex(source)
    }

    /**
     * 计算前面
     */
    fun calculate(): String {
        val request = canonicalRequest()

        val scopeDate = date()
        val scope = scope(scopeDate)

        val date = headers!!.first(AwsS3Utils.Companion.HEADER_DATE)
        val source = source(date, scope, request)
        val sourceHmacSha = sourceHmacSha(source, scopeDate)

        val signedHeaders = signedHeaders()
        return (ALGORITHM + " Credential=" + ak + "/" + scope + "," + "SignedHeaders=" + signedHeaders + ","
                + "Signature=" + sourceHmacSha)
    }

    companion object {
        @JvmField
        val DATETIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")

        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

        protected val HEADER_INCLUDE: Array<String> = arrayOf("host", "content-md5", "range")

        const val ALGORITHM: String = "AWS4-HMAC-SHA256"

        const val SCOPE_SUFFIX: String = "aws4_request"

        @JvmStatic
        fun builder(): S3SingV4Builder {
            return S3SingV4Builder()
        }
    }
}
