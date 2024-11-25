package live.lingting.framework.datascope.annotation

/**
 * 数据权限注解，注解在 Mapper类 或者 对应方法上 用于提供该 mapper 对应表，所需控制的实体信息
 *
 * @author Hccake 2020/9/27
 * @version 1.0
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class DataPermission(
    /**
     * 当前类或方法是否忽略数据权限
     * @return boolean 默认返回 false
     */
    val ignore: Boolean = false,
    /**
     * 仅对指定资源类型进行数据权限控制，只在开启情况下有效，当该数组有值时，exclude不生效
     * @see DataPermission.excludeResources
     *
     * @return 资源类型数组
     */
    val includeResources: Array<String> = [],
    /**
     * 对指定资源类型跳过数据权限控制，只在开启情况下有效，当该includeResources有值时，exclude不生效
     * @see DataPermission.includeResources
     *
     * @return 资源类型数组
     */
    val excludeResources: Array<String> = []
)
