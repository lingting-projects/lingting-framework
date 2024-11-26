package live.lingting.framework.elasticsearch.annotation

import java.lang.annotation.Inherited

/**
 * @author lingting 2024/11/26 14:07
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
annotation class IndexPolymerize()
