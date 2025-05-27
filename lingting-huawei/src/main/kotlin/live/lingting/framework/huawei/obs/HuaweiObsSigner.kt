package live.lingting.framework.huawei.obs

import live.lingting.framework.aws.AwsSigner
import live.lingting.framework.aws.AwsUtils
import live.lingting.framework.crypto.mac.Mac
import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.api.ApiRequest
import live.lingting.framework.http.body.BodySource
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.huawei.HuaweiObs
import live.lingting.framework.huawei.HuaweiUtils
import live.lingting.framework.util.ArrayUtils.contains
import live.lingting.framework.util.DigestUtils
import live.lingting.framework.util.LocalDateTimeUtils.timestamp
import live.lingting.framework.value.multi.StringMultiValue
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.function.BiConsumer

/**
 * @author lingting 2025/5/22 11:17
 */
open class HuaweiObsSigner(
    val method: HttpMethod,
    path: String,
    headers: HttpHeaders,
    val body: BodySource?,
    params: StringMultiValue?,
    ak: String,
    val sk: String,
) : AwsSigner<HuaweiObsSigner, HuaweiObsSigner.Signed>(ak) {

    constructor(
        request: ApiRequest,
        ak: String,
        sk: String,
    ) : this(request.method(), request.path(), request.headers, request.body(), request.params, ak, sk)

    open val headerPrefix = HuaweiObs.HEADER_PREFIX

    open val headerDate = HuaweiUtils.HEADER_DATE

    open val resourceInclude = arrayOf(
        "CDNNotifyConfiguration",
        "acl",
        "append",
        "attname",
        "backtosource",
        "cors",
        "customdomain",
        "delete",
        "deletebucket",
        "directcoldaccess",
        "encryption",
        "inventory",
        "length",
        "lifecycle",
        "location",
        "logging",
        "metadata",
        "mirrorBackToSource",
        "modify",
        "name",
        "notification",
        "obscompresspolicy",
        "orchestration",
        "partNumber",
        "policy",
        "position",
        "quota",
        "rename",
        "replication",
        "response-cache-control",
        "response-content-disposition",
        "response-content-encoding",
        "response-content-language",
        "response-content-type",
        "response-expires",
        "restore",
        "storageClass",
        "storagePolicy",
        "storageinfo",
        "tagging",
        "torrent",
        "truncate",
        "uploadId",
        "uploads",
        "versionId",
        "versioning",
        "versions",
        "website",
        "x-image-process",
        "x-image-save-bucket",
        "x-image-save-object",
        HuaweiObs.HEADER_TOKEN,
        "object-lock",
        "retention"
    )

    open val headers = HttpHeaders.empty().also { it.putAll(headers) }

    open val path = path.let {
        if (path.startsWith("/")) path else "/$path"
    }

    open val bodyPayload by lazy {
        body.let {
            if (it == null || it.length() < 1) {
                ""
            } else {
                DigestUtils.md5Hex(it.string())
            }
        }
    }

    open val params = params.let {
        StringMultiValue().apply {
            params?.forEach { k, vs ->
                if (resourceInclude.contains(k)) {
                    putAll(k, vs)
                }
            }
        }
    }

    open val contentType = headers.contentType() ?: ""

    fun date(time: LocalDateTime): String = AwsUtils.format(time, DateTimeFormatter.RFC_1123_DATE_TIME)

    open fun canonicalUri() = path

    open fun canonicalQuery(): String {
        return HttpUrlBuilder.buildQuery(params, false, sort = true)
    }

    open fun headersForEach(consumer: BiConsumer<String, Collection<String>>) {
        headers.forEachSorted { k, vs ->
            if (!k.startsWith(headerPrefix)) {
                return@forEachSorted
            }
            consumer.accept(k, vs)
        }
    }

    open fun canonicalizedHeaders(): String {
        val builder = StringBuilder()
        headersForEach { k, vs ->
            if (vs.isNotEmpty()) {
                builder.append(k).append(":").append(vs.joinToString(",")).append("\n")
            }
        }
        return builder.toString()
    }

    @JvmOverloads
    fun canonicalizedResource(canonicalQuery: String = canonicalQuery()): String {
        val builder = StringBuilder(path)
        if (canonicalQuery.isNotBlank()) {
            builder.append("?").append(canonicalQuery)
        }
        return builder.toString()
    }

    @JvmOverloads
    open fun source(
        date: String,
        bodyPayload: String = this.bodyPayload,
        canonicalizedHeaders: String = canonicalizedHeaders(),
        canonicalizedResource: String = canonicalizedResource(),
    ): String {
        return "$method\n$bodyPayload\n$contentType\n$date\n$canonicalizedHeaders$canonicalizedResource"
    }

    open fun calculate(time: LocalDateTime, bodyPayload: String = this.bodyPayload): String {
        val date = date(time)
        return calculate(date, bodyPayload)
    }

    open fun calculateByDate(
        date: String,
        bodyPayload: String = ""
    ) = calculate(date, bodyPayload)

    @JvmOverloads
    open fun calculate(
        date: String,
        bodyPayload: String,
        canonicalizedHeaders: String = canonicalizedHeaders(),
        canonicalizedResource: String = canonicalizedResource(),
    ): String {
        val source = source(date, bodyPayload, canonicalizedHeaders, canonicalizedResource)
        return calculate(source)
    }

    open fun calculate(source: String): String {
        val mac = Mac.hmacBuilder().sha1().charset(HuaweiUtils.CHARSET).secret(sk).build()
        return mac.calculateBase64(source)
    }

    open fun authorization(sign: String): String {
        return "OBS $ak:$sign"
    }

    override fun signed(time: LocalDateTime): Signed {
        return signed(time, bodyPayload)
    }

    override fun signed(
        time: LocalDateTime,
        bodyPayload: String
    ): Signed {

        val date = date(time)
        headers.put(headerDate, date)

        val canonicalUri = canonicalUri()
        val canonicalQuery = canonicalQuery()
        val canonicalizedResource = canonicalizedResource(canonicalQuery)
        val canonicalizedHeaders = canonicalizedHeaders()

        val source = source(date, bodyPayload, canonicalizedHeaders, canonicalizedResource)

        val calculate = calculate(source)

        val authorization = authorization(calculate)

        return Signed(
            this,
            headers,
            params,
            contentType,
            bodyPayload,
            canonicalUri,
            canonicalQuery,
            canonicalizedHeaders,
            canonicalizedResource,
            date,
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
        expire: LocalDateTime,
        bodyPayload: String
    ): Signed {
        val date = (expire.timestamp / 1000).toString()

        val token = headers.remove(HuaweiObs.HEADER_TOKEN)?.firstOrNull()
        if (!token.isNullOrEmpty()) {
            params.add(HuaweiObs.HEADER_TOKEN, token)
        }

        val canonicalUri = canonicalUri()
        val canonicalQuery = canonicalQuery()
        val canonicalizedResource = canonicalizedResource(canonicalQuery)
        val canonicalizedHeaders = canonicalizedHeaders()

        val source = source(date, bodyPayload, canonicalizedHeaders, canonicalizedResource)

        val calculate = calculate(source)

        val authorization = authorization(calculate)

        params.add("AccessKeyId", ak)
        params.add("Signature", calculate)
        params.add("Expires", date)

        return Signed(
            this,
            headers,
            params,
            contentType,
            bodyPayload,
            canonicalUri,
            canonicalQuery,
            canonicalizedHeaders,
            canonicalizedResource,
            date,
            source,
            calculate,
            authorization,
        )
    }

    override fun signed(
        time: LocalDateTime,
        duration: Duration
    ): Signed = signed(time, duration, bodyPayload)

    override fun signed(
        time: LocalDateTime,
        duration: Duration,
        bodyPayload: String
    ): Signed {
        val expire = time.plus(duration.seconds, ChronoUnit.SECONDS)
        return signed(time, expire, bodyPayload)
    }

    open class Signed(
        signer: HuaweiObsSigner,
        headers: HttpHeaders,
        params: StringMultiValue?,
        open val contentType: String,
        bodyPayload: String,
        open val canonicalUri: String,
        open val canonicalQuery: String,
        open val canonicalizedHeaders: String,
        open val canonicalizedResource: String,
        open val date: String,
        source: String,
        sign: String,
        authorization: String
    ) : AwsSigner.Signed<HuaweiObsSigner, Signed>(
        signer,
        headers,
        params,
        bodyPayload,
        source,
        sign,
        authorization,
    )

}
