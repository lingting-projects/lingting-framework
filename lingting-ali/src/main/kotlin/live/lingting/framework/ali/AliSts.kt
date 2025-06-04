package live.lingting.framework.ali

import live.lingting.framework.ali.oss.AliOssActions
import live.lingting.framework.ali.properties.AliOssProperties
import live.lingting.framework.ali.properties.AliStsProperties
import live.lingting.framework.ali.sts.AliStsCredentialRequest
import live.lingting.framework.ali.sts.AliStsCredentialResponse
import live.lingting.framework.ali.sts.AliStsRequest
import live.lingting.framework.aws.AwsUtils
import live.lingting.framework.aws.policy.Credential
import live.lingting.framework.aws.policy.Statement
import live.lingting.framework.aws.sts.AwsStsInterface
import live.lingting.framework.time.DatePattern
import live.lingting.framework.util.StringUtils
import java.time.Duration

/**
 * @author lingting 2024-09-14 11:52
 */
open class AliSts(protected val properties: AliStsProperties) : AliClient<AliStsRequest>(properties), AwsStsInterface {

    override fun credential(timeout: Duration, statements: Collection<Statement>): Credential {
        val request = AliStsCredentialRequest()
        request.timeout = timeout.toSeconds()
        request.statements = statements
        request.roleArn = properties.roleArn
        request.roleSessionName = properties.roleSessionName
        return credential(request)
    }

    fun credential(request: AliStsCredentialRequest): Credential {
        val convert = call(request).use {
            it.convert(AliStsCredentialResponse::class.java)
        }
        val ak = convert.accessKeyId
        val sk = convert.accessKeySecret
        val token = convert.securityToken
        val expire = AwsUtils.parse(convert.expire, DatePattern.FORMATTER_ISO_8601)
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
        return ossBucket(properties, AliOssActions.OSS_BUCKET_DEFAULT)
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
        return ossObject(properties, key, AliOssActions.OSS_OBJECT_DEFAULT)
    }

    fun ossObject(properties: AliOssProperties, key: String, vararg actions: String) =
        ossObject(properties, key, actions.toList())

    fun ossObject(properties: AliOssProperties, key: String, actions: Collection<String>): AliOssObject {
        val bucket = properties.bucket
        val statement = Statement.allow()
        statement.addAction(actions)
        statement.addResource("acs:oss:*:*:$bucket/$key")
        val credential = credential(statement)
        val copy = properties.copy()
        copy.useCredential(credential)
        return AliOssObject(copy, key)
    }
    // endregion

}
