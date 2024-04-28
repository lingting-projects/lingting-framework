package live.lingting.framework.mybatis.typehandler;

import com.fasterxml.jackson.core.type.TypeReference;
import live.lingting.framework.jackson.JacksonUtils;
import live.lingting.framework.money.Money;

import java.util.List;

/**
 * @author lingting 2023/1/3 15:28
 */
public class MoneyListTypeHandler extends AbstractListTypeHandler<Money> {

	@Override
	protected List<Money> toObject(String json) {
		return JacksonUtils.toObj(json, new TypeReference<>() {
		});
	}

}
