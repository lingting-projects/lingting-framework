package live.lingting.framework.huawei.obs

import java.time.LocalDateTime
import live.lingting.framework.aws.s3.AwsS3Utils
import live.lingting.framework.crypto.mac.Mac
import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.HttpRequest
import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.body.BodySource
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.huawei.HuaweiObs
import live.lingting.framework.huawei.HuaweiUtils
import live.lingting.framework.util.DigestUtils
import live.lingting.framework.util.StringUtils
import live.lingting.framework.value.multi.StringMultiValue


/**
 * @author lingting 2024/11/5 11:18
 */
class HuaweiObsSing(protected val dateTime: LocalDateTime, protected val method: String?, protected val path: String?, protected val headers: HttpHeaders?, protected val bodySha256: String?, protected val params: StringMultiValue?, protected val region: String?, protected val ak: String?, protected val sk: String?, protected val bucket: String?) {
    class HuaweiObsSingBuilder {
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

        fun dateTime(dateTime: LocalDateTime?): HuaweiObsSingBuilder {
            this.dateTime = dateTime
            return this
        }

        fun method(method: HttpMethod): HuaweiObsSingBuilder {
            return method(method.name)
        }

        fun method(method: String): HuaweiObsSingBuilder {
            this.method = method.uppercase()
            return this
        }

        fun path(path: String?): HuaweiObsSingBuilder {
            this.path = path
            return this
        }

        fun headers(headers: HttpHeaders?): HuaweiObsSingBuilder {
            this.headers = headers
            return this
        }


        fun bodyUnsigned(): HuaweiObsSingBuilder {
            return body(AwsS3Utils.PAYLOAD_UNSIGNED)
        }


        fun body(body: HttpRequest.Body): HuaweiObsSingBuilder {
            return body(body.string())
        }


        fun body(body: BodySource): HuaweiObsSingBuilder {
            return body(body.string())
        }


        fun body(body: String?): HuaweiObsSingBuilder {
            if (AwsS3Utils.PAYLOAD_UNSIGNED == body) {
                return bodySha256(AwsS3Utils.PAYLOAD_UNSIGNED)
            }
            val hex = DigestUtils.sha256Hex(body!!)
            return bodySha256(hex)
        }

        fun bodySha256(bodySha256: String?): HuaweiObsSingBuilder {
            this.bodySha256 = bodySha256
            return this
        }

        fun params(params: StringMultiValue?): HuaweiObsSingBuilder {
            this.params = params
            return this
        }

        fun region(region: String?): HuaweiObsSingBuilder {
            this.region = region
            return this
        }

        fun ak(ak: String?): HuaweiObsSingBuilder {
            this.ak = ak
            return this
        }

        fun sk(sk: String?): HuaweiObsSingBuilder {
            this.sk = sk
            return this
        }

        fun bucket(bucket: String?): HuaweiObsSingBuilder {
            this.bucket = bucket
            return this
        }

        fun build(): HuaweiObsSing {
            val time = if (this.dateTime == null) LocalDateTime.now() else dateTime!!
            return HuaweiObsSing(
                time, this.method, this.path, this.headers, this.bodySha256, this.params,
                this.region, this.ak, this.sk, this.bucket
            )
        }
    }

    fun contentType(): String? {
        val type = headers!!.contentType()
        return if (StringUtils.hasText(type)) type else ""
    }

    fun date(): String {
        return HuaweiUtils.format(dateTime)
    }

    fun canonicalizedHeaders(): String {
        val builder = StringBuilder()
        headers!!.keys().stream().filter { k: String -> k.startsWith(HuaweiObs.HEADER_PREFIX) }.sorted().forEach { k: String ->
            val vs = headers.get(k)
            if (vs!!.isEmpty()) {
                return@forEach
            }
            builder.append(k).append(":").append(java.lang.String.join(",", vs)).append("\n")
        }
        return builder.toString()
    }

    fun query(): String {
        return HttpUrlBuilder.buildQuery(params!!)
    }

    fun canonicalizedResource(): String {
        val query = query()
        return canonicalizedResource(query)
    }

    fun canonicalizedResource(query: String?): String {
        val builder = StringBuilder()
        builder.append("/").append(bucket).append("/")
        if (StringUtils.hasText(path)) {
            builder.append(path, if (path!!.startsWith("/")) 1 else 0, path.length)
        }

        if (StringUtils.hasText(query)) {
            builder.append("?").append(query)
        }
        return builder.toString()
    }

    fun source(): String {
        val md5 = ""
        val type = contentType()
        val date = date()
        val canonicalizedHeaders = canonicalizedHeaders()
        val canonicalizedResource = canonicalizedResource()
        return """
            $method
            $md5
            $type
            $date
            $canonicalizedHeaders$canonicalizedResource
            """.trimIndent()
    }


    fun calculate(): String {
        val source = source()
        val mac = Mac.hmacBuilder().sha1().secret(sk!!).charset(HuaweiUtils.CHARSET).build()
        val base64 = mac.calculateBase64(source)
        return "OBS %s:%s".formatted(ak, base64)
    }

    companion object {
        @JvmStatic
        fun builder(): HuaweiObsSingBuilder {
            return HuaweiObsSingBuilder()
        }
    }
}
