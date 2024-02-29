package live.lingting.framework.mybatis.typehandler;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * @author lingting 2022/9/28 14:43
 */
@Slf4j
public abstract class AbstractSetTypeHandler<T> extends AbstractJacksonTypeHandler<Set<T>> {

	/**
	 * 取出数据转化异常时 使用
	 * @return 实体类对象
	 */
	@Override
	protected Set<T> defaultValue() {
		return new HashSet<>();
	}

	/**
	 * 存储数据异常时 使用
	 * @return 存储数据
	 */
	@Override
	protected String defaultJson() {
		return "[]";
	}

}
