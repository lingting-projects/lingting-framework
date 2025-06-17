package live.lingting.framework.aws.sts

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import live.lingting.framework.aws.AwsResponse

/**
 * @author lingting 2025/6/3 17:41
 */
class AwsStsCredentialResponse : AwsResponse() {

    @JacksonXmlProperty(localName = "AssumeRoleResult")
    var result: Result = Result()

    val accessKeyId: String
        get() = result.credentials.accessKeyId

    val secretAccessKey: String
        get() = result.credentials.secretAccessKey

    val sessionToken: String
        get() = result.credentials.sessionToken

    val expire: String
        get() = result.credentials.expiration

    class Result {

        @JacksonXmlProperty(localName = "Credentials")
        var credentials: Credentials = Credentials()

    }

    class Credentials {

        @JacksonXmlProperty(localName = "AccessKeyId")
        var accessKeyId: String = ""

        @JacksonXmlProperty(localName = "SecretAccessKey")
        var secretAccessKey: String = ""

        @JacksonXmlProperty(localName = "SessionToken")
        var sessionToken: String = ""

        @JacksonXmlProperty(localName = "Expiration")
        var expiration: String = ""

    }

}
