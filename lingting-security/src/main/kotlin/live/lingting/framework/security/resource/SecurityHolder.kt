package live.lingting.framework.security.resource

import live.lingting.framework.security.domain.SecurityScope
import live.lingting.framework.thread.StackThreadLocal


/**
 * @author lingting 2023-03-29 20:29
 */
class SecurityHolder private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        val LOCAL: StackThreadLocal<SecurityScope> = StackThreadLocal()

        fun put(scope: SecurityScope) {
            LOCAL.put(scope)
        }

        fun pop() {
            LOCAL.pop()
        }

        fun get(): SecurityScope? {
            return scope()
        }

        fun option(): Optional<SecurityScope> {
            return scopeOption()
        }

        @JvmStatic
        fun scope(): SecurityScope? {
            return LOCAL.get()
        }

        fun scopeOption(): Optional<SecurityScope> {
            return Optional.ofNullable(scope())
        }

        @JvmStatic
        fun token(): String {
            return scopeOption().map { obj: SecurityScope -> obj.token }.orElse("")
        }

        fun userId(): String? {
            return scopeOption().map { obj: SecurityScope -> obj.userId }.orElse(null)
        }

        fun tenantId(): String? {
            return scopeOption().map { obj: SecurityScope -> obj.tenantId }.orElse(null)
        }

        fun username(): String {
            return scopeOption().map { obj: SecurityScope -> obj.username }.orElse("")
        }

        fun password(): String {
            return scopeOption().map { obj: SecurityScope -> obj.password }.orElse("")
        }

        fun roles(): Set<String?> {
            return scopeOption().map { obj: SecurityScope -> obj.roles }.orElse(emptySet<String>())
        }

        fun permissions(): Set<String?> {
            return scopeOption().map { obj: SecurityScope -> obj.permissions }.orElse(emptySet<String>())
        }
    }
}
