package live.lingting.framework.mybatis.alias

import live.lingting.framework.mybatis.wrapper.LambdaAliasQueryWrapperX

/**
 * 表别名注解，注解在 entity 上，便于构建带别名的查询条件或者查询列
 *
 * @author Hccake 2021/1/14
 * @version 1.0
 * @see LambdaAliasQueryWrapperX
 *
 * @see TableAliasHelper
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class TableAlias(
    /**
     * 当前实体对应的表别名
     * @return String 表别名
     */
    val value: String
)
