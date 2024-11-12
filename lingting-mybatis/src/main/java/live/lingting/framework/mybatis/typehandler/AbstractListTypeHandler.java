package live.lingting.framework.mybatis.typehandler;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lingting 2022/9/28 14:43
 */
public abstract class AbstractListTypeHandler<T> extends AbstractJacksonTypeHandler<List<T>> {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(AbstractListTypeHandler.class);

	/**
	 * 取出数据转化异常时 使用
	 *
	 * @return 实体类对象
	 */
	@Override
	protected List<T> defaultValue() {
		return new ArrayList<>();
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
