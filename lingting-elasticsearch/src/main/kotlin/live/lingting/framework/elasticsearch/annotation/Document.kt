package live.lingting.framework.elasticsearch.annotation

import java.lang.annotation.Inherited

/**
 * @author lingting 2024-03-08 17:57
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
annotation class Document(
    /**
     * 索引名称, 不指定则将类名 由 驼峰转为下划线
     */
    val index: String = ""
)
