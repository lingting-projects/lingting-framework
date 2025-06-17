package live.lingting.framework.aws

import live.lingting.framework.aws.policy.Credential
import live.lingting.framework.aws.policy.Statement
import live.lingting.framework.aws.properties.AwsS3Properties
import live.lingting.framework.aws.properties.AwsStsProperties
import live.lingting.framework.aws.s3.AwsS3Actions
import live.lingting.framework.aws.sts.AwsStsCredentialRequest
import live.lingting.framework.aws.sts.AwsStsCredentialResponse
import live.lingting.framework.aws.sts.AwsStsInterface
import live.lingting.framework.aws.sts.AwsStsRequest
import live.lingting.framework.jackson.JacksonUtils
import live.lingting.framework.time.DatePattern
import live.lingting.framework.util.StringUtils
import java.time.Duration

/**
 * @author lingting 2025/6/3 15:58
 */
class AwsSts(val properties: AwsStsProperties) : AwsClient<AwsStsRequest>(properties), AwsStsInterface {

    override fun service(): String = "sts"

    override fun credential(
        timeout: Duration,
        statements: Collection<Statement>
    ): Credential {
        val request = AwsStsCredentialRequest()
        request.roleArn = properties.roleArn
        request.roleSessionName = properties.roleSessionName
        request.sourceIdentity = properties.sourceIdentity

        request.timeout = timeout
        request.statements = statements
        return credential(request)
    }

    fun credential(request: AwsStsCredentialRequest): Credential {
        val convert = call(request).use {
            val xml = it.string()
            JacksonUtils.xmlToObj(xml, AwsStsCredentialResponse::class.java)
        }
        val ak = convert.accessKeyId
        val sk = convert.secretAccessKey
        val token = convert.sessionToken
        val expire = AwsUtils.parse(convert.expire, DatePattern.FORMATTER_ISO_8601)
        return Credential(ak, sk, token, expire)
    }

    // region s3
    fun s3Bucket(region: String, bucket: String): AwsS3Bucket {
        val s = AwsS3Properties()
        s.region = region
        s.bucket = bucket
        return s3Bucket(s)
    }

    fun s3Bucket(region: String, bucket: String, actions: Collection<String>): AwsS3Bucket {
        val s = AwsS3Properties()
        s.region = region
        s.bucket = bucket
        return s3Bucket(s, actions)
    }

    fun s3Bucket(properties: AwsS3Properties): AwsS3Bucket {
        return s3Bucket(properties, AwsS3Actions.S3_BUCKET_DEFAULT)
    }

    fun s3Bucket(properties: AwsS3Properties, actions: Collection<String>): AwsS3Bucket {
        val bucket = if (StringUtils.hasText(properties.bucket)) properties.bucket else "*"
        val statement = Statement.allow()
        statement.addAction(actions)
        statement.addResource("arn:aws:s3:*:*:$bucket")
        statement.addResource("arn:aws:s3:*:*:$bucket/*")
        val credential = credential(statement)
        val copy = properties.copy()
        copy.useCredential(credential)
        return AwsS3Bucket(copy)
    }

    fun s3Object(region: String, bucket: String, key: String): AwsS3Object {
        val s = AwsS3Properties()
        s.region = region
        s.bucket = bucket
        return s3Object(s, key)
    }

    fun s3Object(region: String, bucket: String, key: String, actions: Collection<String>): AwsS3Object {
        val s = AwsS3Properties()
        s.region = region
        s.bucket = bucket
        return s3Object(s, key, actions)
    }

    fun s3Object(properties: AwsS3Properties, key: String): AwsS3Object {
        return s3Object(properties, key, AwsS3Actions.S3_OBJECT_DEFAULT)
    }

    fun s3Object(properties: AwsS3Properties, key: String, vararg actions: String) =
        s3Object(properties, key, actions.toList())

    fun s3Object(properties: AwsS3Properties, key: String, actions: Collection<String>): AwsS3Object {
        val bucket = properties.bucket
        val statement = Statement.allow()
        statement.addAction(actions)
        statement.addResource("arn:aws:s3:*:*:$bucket/$key")
        val credential = credential(statement)
        val copy = properties.copy()
        copy.useCredential(credential)
        return AwsS3Object(copy, key)
    }
    // endregion

}
