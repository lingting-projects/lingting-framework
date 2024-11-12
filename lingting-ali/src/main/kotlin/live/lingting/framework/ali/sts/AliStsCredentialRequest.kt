package live.lingting.framework.ali.sts;

import live.lingting.framework.aws.policy.Statement;
import live.lingting.framework.http.body.BodySource;
import live.lingting.framework.http.body.MemoryBody;
import live.lingting.framework.jackson.JacksonUtils;

import java.util.Collection;
import java.util.Map;

/**
 * @author lingting 2024-09-14 13:45
 */
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
		Map<String, Object> policy = Map.of("Version", "1", "Statement",
			statements.stream().map(Statement::map).toList());
		Map<String, Object> map = Map.of("RoleArn", roleArn, "RoleSessionName", roleSessionName, "DurationSeconds",
			timeout, "Policy", policy);
		String json = JacksonUtils.toJson(map);
		return new MemoryBody(json);
	}

	public long getTimeout() {return this.timeout;}

	public Collection<Statement> getStatements() {return this.statements;}

	public String getRoleArn() {return this.roleArn;}

	public String getRoleSessionName() {return this.roleSessionName;}

	public void setTimeout(long timeout) {this.timeout = timeout;}

	public void setStatements(Collection<Statement> statements) {this.statements = statements;}

	public void setRoleArn(String roleArn) {this.roleArn = roleArn;}

	public void setRoleSessionName(String roleSessionName) {this.roleSessionName = roleSessionName;}
}
