package live.lingting.framework.elasticsearch.polymerize

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

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

    open fun get(clazz: KClass<out Polymerize>): Polymerize {
        return get(clazz.java)
    }

    protected open fun create(clazz: Class<out Polymerize>): Polymerize {
        return clazz.constructors[0].newInstance() as Polymerize
    }

}
