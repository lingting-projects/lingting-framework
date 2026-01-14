package live.lingting.framework.context

/**
 * @author lingting 2026/1/14 17:52
 */
interface ContextSource {

    val id: String

    fun isAlive(): Boolean

}
