package live.lingting.framework.mybatis.typehandler

/**
 * @author lingting 2022/12/19 16:27
 */
abstract class AbstractMapTypeHandler<K, V> : AbstractJacksonTypeHandler<Map<K, V>>() {
    override fun defaultValue(): Map<K, V> {
        return HashMap()
    }

    override fun defaultJson(): String {
        return "{}"
    }
}
