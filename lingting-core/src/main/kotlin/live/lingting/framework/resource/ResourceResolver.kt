package live.lingting.framework.resource

import live.lingting.framework.Sequence
import java.net.URL
import java.util.function.Predicate

/**
 * @author lingting 2025/10/17 10:01
 */
interface ResourceResolver : Sequence {

    override val sequence: Int
        get() = 0

    fun isSupport(u: URL, protocol: String): Boolean

    fun resolve(u: URL, protocol: String): List<Resource> {
        return resolve(u, protocol, null)
    }

    fun resolve(u: URL, protocol: String, predicate: Predicate<Resource>): List<Resource> {
        return resolve(u, protocol, null, predicate)
    }

    /**
     * @param count 最多分析多少个资源就结束, null 或小于0则无限; 0 为空
     */
    fun resolve(u: URL, protocol: String, count: Int?): List<Resource> {
        return resolve(u, protocol, count) { true }
    }

    /**
     * @param count 最多分析多少个资源就结束, null 或小于0则无限; 0 为空
     */
    fun resolve(u: URL, protocol: String, count: Int?, predicate: Predicate<Resource>): List<Resource>

}
