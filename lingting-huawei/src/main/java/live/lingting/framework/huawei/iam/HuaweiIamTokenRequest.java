package live.lingting.framework.huawei.iam;

import live.lingting.framework.jackson.JacksonUtils;
import lombok.Getter;
import lombok.Setter;

import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lingting 2024-09-12 21:38
 */
@Getter
@Setter
public class HuaweiIamTokenRequest extends HuaweiIamRequest {

	public static final String KEY_PASSWORD = "password";

	protected static final String[] VALUE_METHODS = { KEY_PASSWORD };

	private Map<String, Object> domain;

	private String username;

	private String password;

	@Override
	public boolean usingToken() {
		return false;
	}


	@Override
	public String path() {
		return "v3/auth/tokens";
	}

	@Override
	public HttpRequest.BodyPublisher body() {
		Map<String, Object> map = new HashMap<>();
		map.put("domain", domain);
		map.put("name", username);
		map.put(KEY_PASSWORD, password);

		Map<String, Object> pwd = Map.of("user", map);
		Map<String, Object> identity = Map.of("methods", VALUE_METHODS, KEY_PASSWORD, pwd);
		Map<String, Object> auth = Map.of("identity", identity);
		Map<String, Object> params = Map.of("auth", auth);
		String json = JacksonUtils.toJson(params);
		return HttpRequest.BodyPublishers.ofString(json);
	}

}
