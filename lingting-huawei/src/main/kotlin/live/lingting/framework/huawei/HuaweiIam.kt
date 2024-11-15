package live.lingting.framework.huawei

import live.lingting.framework.aws.policy.Credential
import live.lingting.framework.http.HttpResponse
import live.lingting.framework.http.api.ApiClient
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.huawei.exception.HuaweiIamException
import live.lingting.framework.huawei.iam.HuaweiIamCredentialRequest
import live.lingting.framework.huawei.iam.HuaweiIamCredentialResponse
import live.lingting.framework.huawei.iam.HuaweiIamRequest
import live.lingting.framework.huawei.iam.HuaweiIamToken
import live.lingting.framework.huawei.iam.HuaweiIamTokenRequest
import live.lingting.framework.huawei.iam.HuaweiIamTokenResponse
import live.lingting.framework.huawei.properties.HuaweiIamProperties
import live.lingting.framework.huawei.properties.HuaweiObsProperties
import live.lingting.framework.util.StringUtils
import live.lingting.framework.value.WaitValue
import java.time.Duration
import java.time.LocalDateTime


/**
 * @author lingting 2024-09-12 21:27
 */
class HuaweiIam(@JvmField val properties: HuaweiIamProperties) : ApiClient<HuaweiIamRequest?>(properties.host) {
    // endregion
    /**
     * token 提前多久过期
     */
    var tokenEarlyExpire: Duration = HuaweiUtils.Companion.TOKEN_EARLY_EXPIRE

    /**
     * token 需要外部维护
     */
    var tokenValue: WaitValue<HuaweiIamToken> = WaitValue.of()
        protected set


    override fun customize(request: HuaweiIamRequest, headers: HttpHeaders) {
        if (request.usingToken()) {
            val token = tokenValue.notNull()
            headers.put("X-Auth-Token", token.getValue())
        }
    }

    override fun checkout(request: HuaweiIamRequest, response: HttpResponse): HttpResponse {
        if (response.code() == 401) {
            log.debug("HuaweiIam token expired!")
            refreshToken(true)
            return call(request)
        }

        val string = response.string()
        if (!response.is2xx()) {
            log.error("HuaweiIam request error! uri: {}; code: {}; body:\n{}", response.uri(), response.code(), string)
            throw HuaweiIamException("request error! code: " + response.code())
        }
        return response
    }

    @JvmOverloads
    fun refreshToken(force: Boolean = false) {
        if (!force) {
            val value = tokenValue.value
            if (value != null && !value.isExpired(tokenEarlyExpire)) {
                return
            }
        }

        val token = token()
        tokenValue.update(token)
    }


    fun token(): HuaweiIamToken {
        val request = HuaweiIamTokenRequest()
        request.domain = properties.domain
        request.username = properties.username
        request.password = properties.password

        val response = call(request)
        val convert = response.convert(HuaweiIamTokenResponse::class.java)

        val token = response.headers().first("X-Subject-Token")
        val expire: LocalDateTime = HuaweiUtils.Companion.parse(convert.expire, properties.zone)
        val issued: LocalDateTime = HuaweiUtils.Companion.parse(convert.issued, properties.zone)
        return HuaweiIamToken(token, expire, issued)
    }

    fun credential(statement: HuaweiStatement): Credential {
        return credential(setOf(statement))
    }

    fun credential(statement: HuaweiStatement, vararg statements: HuaweiStatement?): Credential {
        val list: MutableList<HuaweiStatement> = ArrayList(statements.size + 1)
        list.add(statement)
        list.addAll(Arrays.asList(*statements))
        return credential(list)
    }

    fun credential(statements: Collection<HuaweiStatement>?): Credential {
        return credential(HuaweiUtils.Companion.CREDENTIAL_EXPIRE, statements)
    }

    fun credential(timeout: Duration?, statements: Collection<HuaweiStatement>?): Credential {
        val request = HuaweiIamCredentialRequest()
        request.timeout = timeout
        request.statements = statements
        val response = call(request)
        val convert = response.convert(HuaweiIamCredentialResponse::class.java)
        val ak = convert.access
        val sk = convert.secret
        val token = convert.securityToken
        val expire: LocalDateTime = HuaweiUtils.Companion.parse(convert.expire, properties.zone)
        return Credential(ak!!, sk!!, token!!, expire)
    }

    // region obs
    fun obsBucket(region: String?, bucket: String?): HuaweiObsBucket {
        val s = HuaweiObsProperties()
        s.region = region
        s.bucket = bucket
        return obsBucket(s)
    }

    fun obsBucket(region: String?, bucket: String?, actions: Collection<String?>): HuaweiObsBucket {
        val s = HuaweiObsProperties()
        s.region = region
        s.bucket = bucket
        return obsBucket(s, actions)
    }

    fun obsBucket(properties: HuaweiObsProperties): HuaweiObsBucket {
        return obsBucket(properties, HuaweiActions.Companion.OBS_BUCKET_DEFAULT)
    }

    fun obsBucket(properties: HuaweiObsProperties, actions: Collection<String?>): HuaweiObsBucket {
        val bucket = if (StringUtils.hasText(properties.bucket)) properties.bucket else "*"
        val statement: HuaweiStatement = HuaweiStatement.Companion.allow()
        statement.addAction(actions)
        statement.addResource("obs:*:*:bucket:%s".formatted(bucket))
        statement.addResource("obs:*:*:object:%s/*".formatted(bucket))
        val credential = credential(statement)
        val copy = properties.copy()
        copy!!.useCredential(credential)
        return HuaweiObsBucket(copy)
    }

    fun obsObject(region: String?, bucket: String?, key: String?): HuaweiObsObject {
        val s = HuaweiObsProperties()
        s.region = region
        s.bucket = bucket
        return obsObject(s, key)
    }

    fun obsObject(region: String?, bucket: String?, key: String?, actions: Collection<String?>): HuaweiObsObject {
        val s = HuaweiObsProperties()
        s.region = region
        s.bucket = bucket
        return obsObject(s, key, actions)
    }

    fun obsObject(properties: HuaweiObsProperties, key: String?): HuaweiObsObject {
        return obsObject(properties, key, HuaweiActions.Companion.OBS_OBJECT_DEFAULT)
    }

    fun obsObject(properties: HuaweiObsProperties, key: String?, actions: Collection<String?>): HuaweiObsObject {
        val bucket = properties.bucket
        val statement: HuaweiStatement = HuaweiStatement.Companion.allow()
        statement.addAction(actions)
        statement.addResource("obs:*:*:object:%s/%s".formatted(bucket, key))
        val credential = credential(statement)
        val copy = properties.copy()
        copy!!.useCredential(credential)
        return HuaweiObsObject(copy, key)
    }
}
