package live.lingting.framework.security.resource

import java.util.Optional
import live.lingting.framework.context.StackContext
import live.lingting.framework.security.domain.SecurityScope

/**
 * @author lingting 2023-03-29 20:29
 */
object SecurityHolder {

    val LOCAL: StackContext<SecurityScope?> = StackContext()

    @JvmStatic
    fun put(scope: SecurityScope?) {
        LOCAL.push(scope)
    }

    @JvmStatic
    fun pop() {
        LOCAL.pop()
    }

    @JvmStatic
    fun get(): SecurityScope? {
        return scope()
    }

    @JvmStatic
    fun option(): Optional<SecurityScope> {
        return scopeOption()
    }

    @JvmStatic
    fun scope(): SecurityScope? {
        return LOCAL.peek()
    }

    @JvmStatic
    fun scopeOption(): Optional<SecurityScope> {
        return Optional.ofNullable(scope())
    }

    @JvmStatic
    fun authorization(): String {
        return scopeOption().map { it.authorization }.orElse("")
    }

    @JvmStatic
    fun userId(): String? {
        return scopeOption().map { it.userId }.orElse(null)
    }

    @JvmStatic
    fun tenantId(): String? {
        return scopeOption().map { it.tenantId }.orElse(null)
    }

    @JvmStatic
    fun username(): String {
        return scopeOption().map { it.username }.orElse("")
    }

    @JvmStatic
    fun password(): String {
        return scopeOption().map { it.password }.orElse("")
    }

    @JvmStatic
    fun roles(): Set<String> {
        return scopeOption().map { it.roles }.orElse(emptySet<String>())
    }

    @JvmStatic
    fun permissions(): Set<String> {
        return scopeOption().map { it.permissions }.orElse(emptySet<String>())
    }

}
