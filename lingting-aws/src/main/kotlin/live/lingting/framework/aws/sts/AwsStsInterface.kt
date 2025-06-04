package live.lingting.framework.aws.sts

import live.lingting.framework.aws.policy.Credential
import live.lingting.framework.aws.policy.Statement
import java.time.Duration

/**
 * @author lingting 2025/6/3 16:02
 */
interface AwsStsInterface {

    val credentialExpire: Duration
        get() = Duration.ofHours(1)

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
        return credential(credentialExpire, statements)
    }

    fun credential(timeout: Duration, statements: Collection<Statement>): Credential

}
