package live.lingting.framework.huawei.obs

import java.time.LocalDateTime
import live.lingting.framework.crypto.mac.Mac
import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.huawei.HuaweiObs
import live.lingting.framework.huawei.HuaweiUtils
import live.lingting.framework.time.DateTime
import live.lingting.framework.util.StringUtils
import live.lingting.framework.value.multi.StringMultiValue

/**
 * @author lingting 2024/11/5 11:18
 */
open class HuaweiObsSing(
    protected val dateTime: LocalDateTime,
    protected val method: String,
    protected val path: String?,
    protected val headers: HttpHeaders,
    protected val params: StringMultiValue,
    protected val ak: String,
    protected val sk: String,
    protected val bucket: String
) {
    companion object {
        /**
         * 仅以下子资源参与前面
         */
        val RESOURCE_KEYS = listOf(
            "CDNNotifyConfiguration", "acl", "append", "attname", "backtosource", "cors", "customdomain", "delete",
            "deletebucket", "directcoldaccess", "encryption", "inventory", "length", "lifecycle", "location", "logging",
            "metadata", "mirrorBackToSource", "modify", "name", "notification", "obscompresspolicy", "orchestration",
            "partNumber", "policy", "position", "quota", "rename", "replication", "response-cache-control",
            "response-content-disposition", "response-content-encoding", "response-content-language", "response-content-type",
            "response-expires", "restore", "storageClass", "storagePolicy", "storageinfo", "tagging", "torrent", "truncate",
            "uploadId", "uploads", "versionId", "versioning", "versions", "website", "x-image-process",
            "x-image-save-bucket", "x-image-save-object", "x-obs-security-token", "object-lock", "retention"
        )

        @JvmStatic
        fun builder(): HuaweiObsSingBuilder {
            return HuaweiObsSingBuilder()
        }
    }

    private val resources: StringMultiValue = StringMultiValue().also {
        params.forEach { k, vs ->
            if (RESOURCE_KEYS.contains(k)) {
                it.addAll(k, vs)
            }
        }
    }

    fun contentType(): String {
        return headers.contentType() ?: ""
    }

    fun date(): String {
        return HuaweiUtils.format(dateTime)
    }

    fun canonicalizedHeaders(): String {
        val builder = StringBuilder()
        headers.keys().filter { k -> k.startsWith(HuaweiObs.HEADER_PREFIX) }.sorted().forEach { k ->
            val vs = headers.get(k)
            if (vs.isEmpty()) {
                return@forEach
            }
            builder.append(k).append(":").append(java.lang.String.join(",", vs)).append("\n")
        }
        return builder.toString()
    }

    fun query(): String {
        return HttpUrlBuilder.buildQuery(resources)
    }

    fun canonicalizedResource(): String {
        val query = query()
        return canonicalizedResource(query)
    }

    fun canonicalizedResource(query: String): String {
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
        return "$method\n$md5\n$type\n$date\n$canonicalizedHeaders$canonicalizedResource"
    }

    fun calculate(): String {
        val source = source()
        val mac = Mac.hmacBuilder().sha1().secret(sk).charset(HuaweiUtils.CHARSET).build()
        val base64 = mac.calculateBase64(source)
        return "OBS $ak:$base64"
    }

    class HuaweiObsSingBuilder {
        private var dateTime: LocalDateTime? = null

        private var method: String? = null

        private var path: String? = null

        private var headers: HttpHeaders? = null

        private var params: StringMultiValue? = null

        private var ak: String? = null

        private var sk: String? = null

        private var bucket: String? = null

        fun dateTime(dateTime: LocalDateTime): HuaweiObsSingBuilder {
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

        fun path(path: String): HuaweiObsSingBuilder {
            this.path = path
            return this
        }

        fun headers(headers: HttpHeaders): HuaweiObsSingBuilder {
            this.headers = headers
            return this
        }

        fun params(params: StringMultiValue): HuaweiObsSingBuilder {
            this.params = params
            return this
        }

        fun ak(ak: String): HuaweiObsSingBuilder {
            this.ak = ak
            return this
        }

        fun sk(sk: String): HuaweiObsSingBuilder {
            this.sk = sk
            return this
        }

        fun bucket(bucket: String): HuaweiObsSingBuilder {
            this.bucket = bucket
            return this
        }

        fun build(): HuaweiObsSing {
            val time = dateTime ?: DateTime.current()
            return HuaweiObsSing(
                time, this.method!!, this.path, this.headers!!, this.params!!, this.ak!!, this.sk!!, this.bucket!!
            )
        }
    }

}
