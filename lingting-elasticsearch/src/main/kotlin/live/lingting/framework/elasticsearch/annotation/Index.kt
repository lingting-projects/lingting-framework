package live.lingting.framework.elasticsearch.annotation

import java.lang.annotation.Inherited
import kotlin.reflect.KClass
import live.lingting.framework.elasticsearch.polymerize.NonPolymerize
import live.lingting.framework.elasticsearch.polymerize.Polymerize

/**
 * @author lingting 2024-03-08 17:57
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
annotation class Index(
    /**
     * 索引前缀
     */
    val prefix: String = "",
    /**
     * 索引名称, 不指定则将类名 由 驼峰转为下划线
     */
    val index: String = "",
    /**
     * 索引使用的聚合策略
     */
    val polymerize: KClass<out Polymerize> = NonPolymerize::class,
    /**
     * 聚合索引查询限制, 仅查询最近多少个索引. 小于1则不限制
     */
    val polymerizeLimit: Long = 0,
)
