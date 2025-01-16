package live.lingting.framework.aws.s3.properties

import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.policy.Credential
import live.lingting.framework.aws.s3.AwsS3Properties
import live.lingting.framework.aws.s3.enums.HostStyle
import live.lingting.framework.http.HttpUrlBuilder

/**
 * @author lingting 2025/1/14 17:14
 */
abstract class S3Properties {

    var ssl: Boolean = true

    var hostStyle: HostStyle = HostStyle.VIRTUAL

    var region: String = ""

    var endpoint: String = "amazonaws.com"

    var bucket: String = ""

    var acl: Acl = Acl.PRIVATE

    var ak: String = ""

    var sk: String = ""

    var token: String? = ""

    open fun useCredential(credential: Credential) {
        ak = credential.ak
        sk = credential.sk
        token = credential.token
    }

    open fun from(properties: S3Properties) {
        ssl = properties.ssl
        region = properties.region
        endpoint = properties.endpoint
        bucket = properties.bucket
        acl = properties.acl
        ak = properties.ak
        sk = properties.sk
        token = properties.token
    }

    open fun copy(): S3Properties {
        return AwsS3Properties().also { it.from(this) }
    }

    open fun host(): String {
        if (hostStyle == HostStyle.VIRTUAL) {
            return virtualHost()
        }
        return secondHost()
    }

    open fun urlBuilder(): HttpUrlBuilder {
        val builder = HttpUrlBuilder.builder()
            .host(host())

        if (ssl) {
            builder.https()
        } else {
            builder.http()
        }

        if (hostStyle == HostStyle.SECOND) {
            builder.uri(bucket)
        }

        return builder
    }

    open fun virtualHost(): String {
        val host = secondHost()
        if (bucket.isNotBlank()) {
            return "$bucket.$host"
        }
        return host
    }

    abstract fun secondHost(): String

}
