package live.lingting.framework.ali

import java.time.Duration
import java.time.LocalDateTime
import java.util.function.BiConsumer
import live.lingting.framework.ali.exception.AliStsException
import live.lingting.framework.ali.properties.AliOssProperties
import live.lingting.framework.ali.properties.AliStsProperties
import live.lingting.framework.ali.sts.AliStsCredentialRequest
import live.lingting.framework.ali.sts.AliStsCredentialResponse
import live.lingting.framework.ali.sts.AliStsRequest
import live.lingting.framework.aws.policy.Credential
import live.lingting.framework.aws.policy.Statement
import live.lingting.framework.crypto.mac.Mac
import live.lingting.framework.http.HttpResponse
import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.body.BodySource
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.time.DatePattern
import live.lingting.framework.time.DateTime
import live.lingting.framework.util.ArrayUtils.containsIgnoreCase
import live.lingting.framework.util.DigestUtils
import live.lingting.framework.util.StringUtils
import live.lingting.framework.value.multi.StringMultiValue

/**
 * @author lingting 2024-09-14 11:52
 */
open class AliSts(protected val properties: AliStsProperties) : AliClient<AliStsRequest>(properties) {

    companion object {
        const val ALGORITHM: String = "ACS3-HMAC-SHA256"

        const val HEADER_PREFIX: String = "x-acs"
    }

    override fun customize(request: AliStsRequest, headers: HttpHeaders) {
        val name = request.name()
        val version = request.version()
        val nonce = request.nonce()

        headers.put("x-acs-action", name)
        headers.put("x-acs-version", version)
        headers.put("x-acs-signature-nonce", nonce)

        if (StringUtils.hasText(token)) {
            headers.put("x-acs-security-token", token!!)
        }
    }

    override fun checkout(request: AliStsRequest, response: HttpResponse): HttpResponse {
        if (!response.is2xx) {
            val string = response.string()
            log.error("AliSts call error! uri: {}; code: {}; body:\n{}", response.uri(), response.code(), string)
            throw AliStsException("request error! code: " + response.code())
        }
        return response
    }

    override fun customize(
        request: AliStsRequest, headers: HttpHeaders, requestBody: BodySource,
        params: StringMultiValue
    ) {
        val now = DateTime.current()
        val date: String = AliUtils.format(now, DatePattern.FORMATTER_ISO_8601)
        headers.put("x-acs-date", date)

        val method = request.method().name
        val path = request.uri()
        val uri = if (StringUtils.hasText(path)) path else "/"
        val query = HttpUrlBuilder.buildQuery(params)
        val body = requestBody.string()
        val bodySha = DigestUtils.sha256Hex(body)

        headers.put("x-acs-content-sha256", bodySha)

        val headerBuilder = StringBuilder()
        val signHeaderBuilder = StringBuilder()
        headers.forEachSorted(BiConsumer { k, vs ->
            if (!k.startsWith(HEADER_PREFIX) && !HEADER_INCLUDE.containsIgnoreCase(k)) {
                return@BiConsumer
            }
            for (v in vs) {
                headerBuilder.append(k).append(":").append(v).append("\n")
            }
            signHeaderBuilder.append(k).append(";")
        })
        if (signHeaderBuilder.isNotBlank()) {
            signHeaderBuilder.deleteCharAt(signHeaderBuilder.length - 1)
        }
        val header = headerBuilder.toString()
        val signHeader = signHeaderBuilder.toString()

        val requestString = "$method\n$uri\n$query\n$header\n$signHeader\n$bodySha"
        val requestSha = DigestUtils.sha256Hex(requestString)

        val source = "$ALGORITHM\n$requestSha"
        val sourceHmacSha = Mac.hmacBuilder().sha256().secret(sk).build().calculateHex(source)

        val authorization = ("$ALGORITHM Credential=$ak,SignedHeaders=$signHeader,Signature=$sourceHmacSha")
        headers.authorization(authorization)
    }

    fun credential(statement: Statement): Credential {
        return credential(setOf(statement))
    }

    fun credential(statement: Statement, vararg statements: Statement): Credential {
        val list: MutableList<Statement> = ArrayList(statements.size + 1)
        list.add(statement)
        list.addAll(statements)
        return credential(list)
    }

    fun credential(statements: Collection<Statement>): Credential {
        return credential(AliUtils.CREDENTIAL_EXPIRE, statements)
    }

    fun credential(timeout: Duration, statements: Collection<Statement>): Credential {
        val request = AliStsCredentialRequest()
        request.timeout = timeout.toSeconds()
        request.statements = statements
        request.roleArn = properties.roleArn
        request.roleSessionName = properties.roleSessionName
        val response = call(request)
        val convert = response.convert(AliStsCredentialResponse::class.java)
        val ak = convert.accessKeyId
        val sk = convert.accessKeySecret
        val token = convert.securityToken
        val expire: LocalDateTime = AliUtils.parse(convert.expire)
        return Credential(ak, sk, token, expire)
    }

    // region oss
    fun ossBucket(region: String, bucket: String): AliOssBucket {
        val s = AliOssProperties()
        s.region = region
        s.bucket = bucket
        return ossBucket(s)
    }

    fun ossBucket(region: String, bucket: String, actions: Collection<String>): AliOssBucket {
        val s = AliOssProperties()
        s.region = region
        s.bucket = bucket
        return ossBucket(s, actions)
    }

    fun ossBucket(properties: AliOssProperties): AliOssBucket {
        return ossBucket(properties, AliActions.OSS_BUCKET_DEFAULT)
    }

    fun ossBucket(properties: AliOssProperties, actions: Collection<String>): AliOssBucket {
        val bucket = if (StringUtils.hasText(properties.bucket)) properties.bucket else "*"
        val statement = Statement.allow()
        statement.addAction(actions)
        statement.addResource("acs:oss:*:*:$bucket")
        statement.addResource("acs:oss:*:*:$bucket/*")
        val credential = credential(statement)
        val copy = properties.copy()
        copy.useCredential(credential)
        return AliOssBucket(copy)
    }

    fun ossObject(region: String, bucket: String, key: String): AliOssObject {
        val s = AliOssProperties()
        s.region = region
        s.bucket = bucket
        return ossObject(s, key)
    }

    fun ossObject(region: String, bucket: String, key: String, actions: Collection<String>): AliOssObject {
        val s = AliOssProperties()
        s.region = region
        s.bucket = bucket
        return ossObject(s, key, actions)
    }

    fun ossObject(properties: AliOssProperties, key: String): AliOssObject {
        return ossObject(properties, key, AliActions.OSS_OBJECT_DEFAULT)
    }

    fun ossObject(properties: AliOssProperties, key: String, actions: Collection<String>): AliOssObject {
        val bucket = properties.bucket
        val statement = Statement.allow()
        statement.addAction(actions)
        statement.addResource("acs:oss:*:*:$bucket/$key")
        val credential = credential(statement)
        val copy = properties.copy()
        copy.useCredential(credential)
        return AliOssObject(copy, key)
    } // endregion

}
