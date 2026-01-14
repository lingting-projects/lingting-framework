package live.lingting.framework.context

/**
 * @author lingting 2026/1/14 17:53
 */
class ThreadContextSource(val thread: Thread) : ContextSource {

    override val id: String = thread.threadId().toString()

    override fun isAlive(): Boolean {
        return thread.isAlive
    }

    override fun equals(other: Any?): Boolean {
        if (thread == other) {
            return true
        }
        if (other !is ContextSource) {
            return false
        }
        return id == other.id
    }

    override fun hashCode(): Int {
        return thread.hashCode()
    }

}
