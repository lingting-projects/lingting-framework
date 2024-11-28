package live.lingting.framework.huawei

import live.lingting.framework.aws.policy.Statement

/**
 * @author lingting 2024-09-13 13:47
 */
class HuaweiStatement(allow: Boolean) : Statement(allow) {
    val conditions: LinkedHashMap<String, LinkedHashMap<String, LinkedHashSet<String>>> = LinkedHashMap()

    fun putCondition(operator: String, value: Map<String, Collection<String>>) {
        val map = LinkedHashMap<String, LinkedHashSet<String>>()
        for ((key, value1) in value) {
            map[key] = LinkedHashSet(value1)
        }
        conditions[operator] = map
    }

    override fun map(): MutableMap<String, Any> {
        val map = super.map()
        if (conditions.isNotEmpty()) {
            map.put("Condition", conditions)
        }
        return map
    }

    companion object {
        @JvmStatic
        fun allow(): HuaweiStatement {
            return HuaweiStatement(true)
        }

        @JvmStatic
        fun deny(): HuaweiStatement {
            return HuaweiStatement(false)
        }
    }
}
