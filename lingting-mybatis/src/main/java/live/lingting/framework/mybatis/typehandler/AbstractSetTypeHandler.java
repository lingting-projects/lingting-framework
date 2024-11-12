package live.lingting.framework.mybatis.typehandler;

import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * @author lingting 2022/9/28 14:43
 */
public abstract class AbstractSetTypeHandler<T> extends AbstractJacksonTypeHandler<Set<T>> {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(AbstractSetTypeHandler.class);

	/**
	 * 取出数据转化异常时 使用
	 *
	 * @return 实体类对象
	 */
	@Override
	protected Set<T> defaultValue() {
		return new HashSet<>();
	}

	/**
	 * 存储数据异常时 使用
	 *
	 * @return 存储数据
	 */
	@Override
	protected String defaultJson() {
		return "[]";
	}

}
