package live.lingting.framework.ali.sts

import live.lingting.framework.aws.policy.Statement
import live.lingting.framework.http.body.BodySource
import live.lingting.framework.http.body.MemoryBody
import live.lingting.framework.jackson.JacksonUtils

/**
 * @author lingting 2024-09-14 13:45
 */
class AliStsCredentialRequest : AliStsRequest() {
    /**
     * 过期时长, 单位: 秒
     */
    var timeout: Long = 0

    var statements: Collection<Statement> = emptyList()

    var roleArn: String = ""

    var roleSessionName: String = ""

    override fun name(): String {
        return "AssumeRole"
    }

    override fun version(): String {
        return "2015-04-01"
    }

    override fun path(): String {
        return ""
    }

    override fun body(): BodySource {
        val policy = mapOf(
            "Version" to "1",
            "Statement" to statements.map { obj -> obj.map() }.toList(),
        )
        val map = mapOf(
            "RoleArn" to roleArn,
            "RoleSessionName" to roleSessionName,
            "DurationSeconds" to timeout,
            "Policy" to policy
        )
        val json = JacksonUtils.toJson(map)
        return MemoryBody(json)
    }
}
