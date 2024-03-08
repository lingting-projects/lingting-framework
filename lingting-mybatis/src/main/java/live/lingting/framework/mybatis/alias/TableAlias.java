package live.lingting.framework.mybatis.alias;

import live.lingting.framework.mybatis.wrapper.LambdaAliasQueryWrapperX;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表别名注解，注解在 entity 上，便于构建带别名的查询条件或者查询列
 *
 * @author Hccake 2021/1/14
 * @version 1.0
 * @see LambdaAliasQueryWrapperX
 * @see TableAliasHelper
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TableAlias {

	/**
	 * 当前实体对应的表别名
	 * @return String 表别名
	 */
	String value();

}
