package live.lingting.framework.s3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @author lingting 2024-09-12 20:31
 */
@Getter
@Setter
@RequiredArgsConstructor
public class Statement {

	protected final boolean allow;

	protected final LinkedHashSet<String> actions = new LinkedHashSet<>();

	protected final LinkedHashSet<String> resources = new LinkedHashSet<>();

	public static Statement allow() {
		return new Statement(true);
	}

	public static Statement deny() {
		return new Statement(false);
	}

	public void addAction(String action) {
		actions.add(action);
	}

	public void addAction(String... actions) {
		addAction(Arrays.asList(actions));
	}

	public void addAction(Collection<String> actions) {
		for (String action : actions) {
			addAction(action);
		}
	}

	public void addResource(String resource) {
		resources.add(resource);
	}

	public void addResource(String... resources) {
		addResource(Arrays.asList(resources));
	}

	public void addResource(Collection<String> resources) {
		for (String resource : resources) {
			addResource(resource);
		}
	}

	public Map<String, Object> map() {
		Map<String, Object> map = new HashMap<>(4);
		map.put("Effect", allow ? "Allow" : "Deny");
		map.put("Action", new LinkedHashSet<>(actions));
		map.put("Resource", new LinkedHashSet<>(resources));
		return map;
	}

}
