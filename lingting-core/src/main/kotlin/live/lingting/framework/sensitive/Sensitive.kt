package live.lingting.framework.sensitive

import java.lang.annotation.Inherited
import kotlin.reflect.KClass
import live.lingting.framework.sensitive.serializer.SensitiveDefaultSerializer

/**
 * @author lingting 2023-04-27 15:15
 */
@Inherited
@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Sensitive(
    val value: KClass<out SensitiveSerializer> = SensitiveDefaultSerializer::class,
    val middle: String = SensitiveUtils.MIDDLE,
    val prefixLength: Int = -1,
    val suffixLength: Int = -1,
)
