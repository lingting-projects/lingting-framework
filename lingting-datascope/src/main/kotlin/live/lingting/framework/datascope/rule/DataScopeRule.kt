package live.lingting.framework.datascope.rule

import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER
import kotlin.annotation.AnnotationTarget.PROPERTY_SETTER

/**
 * 数据范围注解
 * @author Hccake 2020/9/27
 * @version 1.0
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(CLASS, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
annotation class DataScopeRule(
    /**
     * 当前类或方法是否忽略数据范围(对所有操作类型有效, 优先级高)
     * @return boolean 默认返回 false
     */
    val ignore: Boolean = false,

    val ignoreQuery: Boolean = false,

    val ignoreUpdate: Boolean = false,

    val ignoreDelete: Boolean = false,

    /**
     * 仅对指定资源类型进行数据范围控制，只在开启情况下有效，当该数组有值时，exclude不生效(对所有操作类型有效, 优先级高)
     * @see DataScopeRule.excludeResources
     * @return 资源类型数组
     */
    val includeResources: Array<String> = [],

    val includeQueryResources: Array<String> = [],

    val includeUpdateResources: Array<String> = [],

    val includeDeleteResources: Array<String> = [],

    /**
     * 对指定资源类型跳过数据范围控制，只在开启情况下有效，当该includeResources有值时，exclude不生效(对所有操作类型有效, 优先级高)
     * @see DataScopeRule.includeResources
     * @return 资源类型数组
     */
    val excludeResources: Array<String> = [],

    val excludeQueryResources: Array<String> = [],

    val excludeUpdateResources: Array<String> = [],

    val excludeDeleteResources: Array<String> = []

)
