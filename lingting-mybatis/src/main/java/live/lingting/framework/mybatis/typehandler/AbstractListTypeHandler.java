package live.lingting.framework.mybatis.typehandler;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lingting 2022/9/28 14:43
 */
@Slf4j
public abstract class AbstractListTypeHandler<T> extends AbstractJacksonTypeHandler<List<T>> {

	/**
	 * 取出数据转化异常时 使用
	 * @return 实体类对象
	 */
	@Override
	protected List<T> defaultValue() {
		return new ArrayList<>();
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
