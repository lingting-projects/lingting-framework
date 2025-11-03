package live.lingting.framework.util

import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * @author lingting 2024/12/5 19:33
 */
object FieldUtils {

    @JvmStatic
    inline val Field?.isPublic: Boolean get() = if (this == null) false else Modifier.isPublic(modifiers)

    @JvmStatic
    inline val Field?.isProtected: Boolean get() = if (this == null) false else Modifier.isProtected(modifiers)

    @JvmStatic
    inline val Field?.isPrivate: Boolean get() = if (this == null) false else Modifier.isPrivate(modifiers)

    @JvmStatic
    inline val Field?.isStatic: Boolean get() = if (this == null) false else Modifier.isStatic(modifiers)

    @JvmStatic
    inline val Field?.isFinal: Boolean get() = if (this == null) false else Modifier.isFinal(modifiers)

    @JvmStatic
    inline val Field?.isVolatile: Boolean get() = if (this == null) false else Modifier.isVolatile(modifiers)

}
