package live.lingting.framework.huawei.iam;

import live.lingting.framework.http.body.BodySource;
import live.lingting.framework.http.body.MemoryBody;
import live.lingting.framework.huawei.HuaweiStatement;
import live.lingting.framework.jackson.JacksonUtils;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lingting 2024-09-13 13:53
 */
public class HuaweiIamCredentialRequest extends HuaweiIamRequest {

	protected static final String[] VALUE_METHODS = {"token"};

	protected Duration timeout;

	protected Collection<HuaweiStatement> statements;

	@Override
	public String path() {
		return "v3.0/OS-CREDENTIAL/securitytokens";
	}

	@Override
	public BodySource body() {
		Map<String, Object> policy = new HashMap<>();
		policy.put("Version", "1.1");
		policy.put("Statement", statements.stream().map(HuaweiStatement::map).toList());

		Map<String, Object> token = Map.of("duration_seconds", timeout.getSeconds());

		Map<String, Object> identity = Map.of("methods", VALUE_METHODS, "token", token, "policy", policy);
		Map<String, Object> auth = Map.of("identity", identity);
		Map<String, Object> params = Map.of("auth", auth);
		String json = JacksonUtils.toJson(params);
		return new MemoryBody(json);
	}

	public Duration getTimeout() {return this.timeout;}

	public Collection<HuaweiStatement> getStatements() {return this.statements;}

	public void setTimeout(Duration timeout) {this.timeout = timeout;}

	public void setStatements(Collection<HuaweiStatement> statements) {this.statements = statements;}
}
