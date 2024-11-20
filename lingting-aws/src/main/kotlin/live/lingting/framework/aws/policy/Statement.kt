package live.lingting.framework.aws.policy

import java.util.Arrays


/**
 * @author lingting 2024-09-12 20:31
 */
open class Statement(val isAllow: Boolean) {
    val actions: LinkedHashSet<String> = LinkedHashSet()

    val resources: LinkedHashSet<String> = LinkedHashSet()

    fun addAction(action: String) {
        actions.add(action)
    }

    fun addAction(vararg actions: String) {
        addAction(actions.toList())
    }

    fun addAction(actions: Collection<String>) {
        for (action in actions) {
            addAction(action)
        }
    }

    fun addResource(resource: String) {
        resources.add(resource)
    }

    fun addResource(vararg resources: String) {
        addResource(Arrays.asList(*resources))
    }

    fun addResource(resources: Collection<String>) {
        for (resource in resources) {
            addResource(resource)
        }
    }

    open fun map(): MutableMap<String, Any> {
        val map: MutableMap<String, Any> = HashMap(4)
        map["Effect"] = if (isAllow) "Allow" else "Deny"
        map["Action"] = LinkedHashSet(actions)
        map["Resource"] = LinkedHashSet(resources)
        return map
    }

    companion object {
        @JvmStatic
        fun allow(): Statement {
            return Statement(true)
        }

        @JvmStatic
        fun deny(): Statement {
            return Statement(false)
        }
    }
}
