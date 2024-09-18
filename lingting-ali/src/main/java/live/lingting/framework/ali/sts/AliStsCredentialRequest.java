package live.lingting.framework.ali.sts;

import live.lingting.framework.http.HttpUrlBuilder;
import live.lingting.framework.jackson.JacksonUtils;
import live.lingting.framework.s3.Statement;
import lombok.Getter;
import lombok.Setter;

import java.net.http.HttpRequest;
import java.util.Collection;
import java.util.Map;

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
	public void configure(HttpUrlBuilder builder) {
		builder.uri("");
	}

	@Override
	public HttpRequest.BodyPublisher body() {
		Map<String, Object> policy = Map.of("Version", "1", "Statement",
				statements.stream().map(Statement::map).toList());
		Map<String, Object> map = Map.of("RoleArn", roleArn, "RoleSessionName", roleSessionName, "DurationSeconds",
				timeout, "Policy", policy);
		String json = JacksonUtils.toJson(map);
		return HttpRequest.BodyPublishers.ofString(json);
	}

}
