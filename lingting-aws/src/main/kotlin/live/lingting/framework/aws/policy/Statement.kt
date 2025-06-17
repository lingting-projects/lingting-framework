package live.lingting.framework.aws.policy

/**
 * @author lingting 2024-09-12 20:31
 */
open class Statement(val isAllow: Boolean) {

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

    protected val actions: LinkedHashSet<String> = LinkedHashSet()

    protected val resources: LinkedHashSet<String> = LinkedHashSet()

    protected val conditions: LinkedHashMap<String, LinkedHashMap<String, LinkedHashSet<String>>> = LinkedHashMap()

    open fun addAction(action: String) {
        actions.add(action)
    }

    open fun addAction(vararg actions: String) {
        addAction(actions.toList())
    }

    open fun addAction(actions: Collection<String>) {
        for (action in actions) {
            addAction(action)
        }
    }

    open fun addResource(resource: String) {
        resources.add(resource)
    }

    open fun addResource(vararg resources: String) {
        addResource(resources.toList())
    }

    open fun addResource(resources: Collection<String>) {
        for (resource in resources) {
            addResource(resource)
        }
    }

    open fun putCondition(operator: String, value: Map<String, Collection<String>>) {
        val map = LinkedHashMap<String, LinkedHashSet<String>>()
        for ((key, value1) in value) {
            map[key] = LinkedHashSet(value1)
        }
        conditions[operator] = map
    }

    open fun map(): MutableMap<String, Any> {
        val map: MutableMap<String, Any> = HashMap(4)
        map["Effect"] = if (isAllow) "Allow" else "Deny"
        map["Action"] = LinkedHashSet(actions)
        map["Resource"] = LinkedHashSet(resources)
        if (conditions.isNotEmpty()) {
            map.put("Condition", conditions)
        }
        return map
    }

}
