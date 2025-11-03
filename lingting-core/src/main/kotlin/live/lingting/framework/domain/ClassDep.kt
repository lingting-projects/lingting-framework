package live.lingting.framework.domain

/**
 * @author lingting 2025/9/15 18:53
 */
class ClassDep(
    val self: Class<*>,
    val supper: Class<*>,
    val interfaces: Array<Class<*>>,
) {

    override fun equals(other: Any?): Boolean {
        if (other !is ClassDep) return false
        return self == other.self
    }

    override fun hashCode(): Int {
        return self.hashCode()
    }

    fun isFirst(): Boolean {
        return Object::class.java.isAssignableFrom(supper)
    }

}
