package live.lingting.framework.huawei.iam;

import live.lingting.framework.http.body.MemoryBody;
import live.lingting.framework.jackson.JacksonUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lingting 2024-09-12 21:38
 */
public class HuaweiIamTokenRequest extends HuaweiIamRequest {

	public static final String KEY_PASSWORD = "password";

	protected static final String[] VALUE_METHODS = {KEY_PASSWORD};

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
	public MemoryBody body() {
		Map<String, Object> map = new HashMap<>();
		map.put("domain", domain);
		map.put("name", username);
		map.put(KEY_PASSWORD, password);

		Map<String, Object> pwd = Map.of("user", map);
		Map<String, Object> identity = Map.of("methods", VALUE_METHODS, KEY_PASSWORD, pwd);
		Map<String, Object> auth = Map.of("identity", identity);
		Map<String, Object> params = Map.of("auth", auth);
		String json = JacksonUtils.toJson(params);
		return new MemoryBody(json);
	}

	public Map<String, Object> getDomain() {return this.domain;}

	public String getUsername() {return this.username;}

	public String getPassword() {return this.password;}

	public void setDomain(Map<String, Object> domain) {this.domain = domain;}

	public void setUsername(String username) {this.username = username;}

	public void setPassword(String password) {this.password = password;}
}
