package live.lingting.framework.grpc.exception

import kotlin.reflect.KClass

/**
 * @author lingting 2024-03-27 09:43
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class GrpcExceptionHandler(vararg val value: KClass<out Throwable> = [])
