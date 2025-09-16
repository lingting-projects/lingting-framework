package live.lingting.framework.domain

/**
 * @author lingting 2025/9/15 18:59
 */
class ClassDepTree(
    val self: ClassDep,
    val children: Array<ClassDepTree>
) {

    override fun hashCode(): Int {
        return self.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ClassDepTree) return false
        return self == other.self
    }

}
