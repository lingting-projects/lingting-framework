package live.lingting.framework.mybatis.typehandler;

import com.fasterxml.jackson.core.type.TypeReference;
import live.lingting.framework.jackson.JacksonUtils;
import live.lingting.framework.money.Money;

import java.util.Set;

public class MoneySetTypeHandler extends AbstractSetTypeHandler<Money> {

	@Override
	protected Set<Money> toObject(String json) {
		return JacksonUtils.toObj(json, new TypeReference<>() {
		});
	}

}
