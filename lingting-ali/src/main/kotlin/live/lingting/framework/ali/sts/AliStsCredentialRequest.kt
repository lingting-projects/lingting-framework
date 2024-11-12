package live.lingting.framework.ali.sts

import live.lingting.framework.aws.policy.Statement
import live.lingting.framework.http.body.BodySource
import live.lingting.framework.http.body.MemoryBody
import live.lingting.framework.jackson.JacksonUtils
import java.util.Map

/**
 * @author lingting 2024-09-14 13:45
 */
class AliStsCredentialRequest : AliStsRequest() {
    /**
     * 过期时长, 单位: 秒
     */
    var timeout: Long = 0

    var statements: Collection<Statement>? = null

    var roleArn: String? = null

    var roleSessionName: String? = null

    override fun name(): String {
        return "AssumeRole"
    }

    override fun version(): String {
        return "2015-04-01"
    }

    override fun path(): String {
        return ""
    }

    override fun body(): BodySource? {
        val policy = Map.of(
            "Version", "1", "Statement",
            statements!!.stream().map { obj: Statement -> obj.map() }.toList()
        )
        val map = Map.of(
            "RoleArn", roleArn, "RoleSessionName", roleSessionName, "DurationSeconds",
            timeout, "Policy", policy
        )
        val json = JacksonUtils.toJson(map)
        return MemoryBody(json)
    }
}
