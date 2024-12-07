package live.lingting.framework.util

import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * @author lingting 2024/12/5 19:37
 */
object MethodUtils {
    @JvmStatic
    inline val Method.isPublic: Boolean get() = Modifier.isPublic(modifiers)

    @JvmStatic
    inline val Method.isProtected: Boolean get() = Modifier.isProtected(modifiers)

    @JvmStatic
    inline val Method.isPrivate: Boolean get() = Modifier.isPrivate(modifiers)

    @JvmStatic
    inline val Method.isStatic: Boolean get() = Modifier.isStatic(modifiers)

    @JvmStatic
    inline val Method.isFinal: Boolean get() = Modifier.isFinal(modifiers)

    @JvmStatic
    inline val Method.isAbstract: Boolean get() = Modifier.isAbstract(modifiers)

    @JvmStatic
    inline val Method.isNative: Boolean get() = Modifier.isNative(modifiers)

    @JvmStatic
    inline val Method.isSynchronized: Boolean get() = Modifier.isSynchronized(modifiers)

}
