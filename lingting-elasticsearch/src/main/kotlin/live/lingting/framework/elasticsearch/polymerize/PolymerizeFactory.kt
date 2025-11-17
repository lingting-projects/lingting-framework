package live.lingting.framework.elasticsearch.polymerize

import java.util.concurrent.ConcurrentHashMap

/**
 * @author lingting 2024/11/26 13:43
 */
open class PolymerizeFactory {

    companion object {

        private val cache = ConcurrentHashMap<Class<out Polymerize>, Polymerize>()

    }

    open fun get(clazz: Class<out Polymerize>): Polymerize {
        return cache.computeIfAbsent(clazz) { create(it) }
    }

    protected open fun create(clazz: Class<out Polymerize>): Polymerize {
        return clazz.constructors[0].newInstance() as Polymerize
    }

}
