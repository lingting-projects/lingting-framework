package live.lingting.framework.huawei;

import live.lingting.framework.s3.Statement;
import live.lingting.framework.util.CollectionUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @author lingting 2024-09-13 13:47
 */
@Getter
@Setter
public class HuaweiStatement extends Statement {

	protected final LinkedHashMap<String, LinkedHashMap<String, LinkedHashSet<String>>> conditions = new LinkedHashMap<>();

	public HuaweiStatement(boolean allow) {
		super(allow);
	}


	public static HuaweiStatement allow() {
		return new HuaweiStatement(true);
	}

	public static HuaweiStatement deny() {
		return new HuaweiStatement(false);
	}


	public void putCondition(String operator, Map<String, Collection<String>> value) {
		LinkedHashMap<String, LinkedHashSet<String>> map = new LinkedHashMap<>();
		for (Map.Entry<String, Collection<String>> entry : value.entrySet()) {
			map.put(entry.getKey(), new LinkedHashSet<>(entry.getValue()));
		}
		conditions.put(operator, map);
	}

	@Override
	public Map<String, Object> map() {
		Map<String, Object> map = super.map();
		if (!CollectionUtils.isEmpty(conditions)) {
			map.put("Condition", conditions);
		}
		return map;
	}

}
