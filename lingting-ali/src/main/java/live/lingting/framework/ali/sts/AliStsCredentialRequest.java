package live.lingting.framework.ali.sts;

import live.lingting.framework.aws.policy.Statement;
import live.lingting.framework.http.body.BodySource;
import live.lingting.framework.http.body.MemoryBody;
import live.lingting.framework.jackson.JacksonUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lingting 2024-09-14 13:45
 */
@Getter
@Setter
public class AliStsCredentialRequest extends AliStsRequest {

	/**
	 * 过期时长, 单位: 秒
	 */
	protected long timeout;

	protected Collection<Statement> statements;

	protected String roleArn;

	protected String roleSessionName;

	@Override
	public String name() {
		return "AssumeRole";
	}

	@Override
	public String version() {
		return "2015-04-01";
	}

	@Override
	public String path() {
		return "";
	}

	@Override
	public BodySource body() {
		Map<String, Object> policy = new HashMap<>();
		policy.put("Version", "1");
		policy.put("Statement", statements.stream().map(Statement::map).collect(Collectors.toList()));
		Map<String, Object> map = new HashMap<>();
		map.put("RoleArn", roleArn);
		map.put("RoleSessionName", roleSessionName);
		map.put("DurationSeconds", timeout);
		map.put("Policy", policy);
		String json = JacksonUtils.toJson(map);
		return MemoryBody.of(json);
	}

}
