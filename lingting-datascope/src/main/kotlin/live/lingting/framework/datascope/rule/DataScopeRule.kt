package live.lingting.framework.datascope.rule

/**
 * 数据范围注解
 * @author Hccake 2020/9/27
 * @version 1.0
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class DataScopeRule(
    /**
     * 当前类或方法是否忽略数据范围
     * @return boolean 默认返回 false
     */
    val ignore: Boolean = false,
    /**
     * 仅对指定资源类型进行数据范围控制，只在开启情况下有效，当该数组有值时，exclude不生效
     * @see DataScopeRule.excludeResources
     * @return 资源类型数组
     */
    val includeResources: Array<String> = [],
    /**
     * 对指定资源类型跳过数据范围控制，只在开启情况下有效，当该includeResources有值时，exclude不生效
     * @see DataScopeRule.includeResources
     * @return 资源类型数组
     */
    val excludeResources: Array<String> = []
)
