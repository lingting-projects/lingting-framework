package live.lingting.framework.resource

import live.lingting.framework.Sequence
import java.net.URL

/**
 * @author lingting 2025/10/17 10:01
 */
interface ResourceResolver : Sequence {

    override val sequence: Int
        get() = 0

    fun isSupport(u: URL, protocol: String): Boolean

    fun resolve(u: URL, protocol: String): List<Resource>

}
