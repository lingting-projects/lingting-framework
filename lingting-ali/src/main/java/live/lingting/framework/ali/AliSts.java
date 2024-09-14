package live.lingting.framework.ali;

import live.lingting.framework.ali.exception.AliStsException;
import live.lingting.framework.ali.properties.AliStsProperties;
import live.lingting.framework.ali.sts.AliStsCredentialRequest;
import live.lingting.framework.ali.sts.AliStsCredentialResponse;
import live.lingting.framework.http.HttpResponse;
import live.lingting.framework.s3.Credential;
import live.lingting.framework.s3.Statement;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static live.lingting.framework.ali.AliUtils.CREDENTIAL_EXPIRE;

/**
 * @author lingting 2024-09-14 11:52
 */
public class AliSts extends AliClient {

	protected final AliStsProperties properties;

	public AliSts(AliStsProperties properties) {
		super(properties);
		this.properties = properties;
	}

	public Credential credential(Statement statement) {
		return credential(Collections.singleton(statement));
	}

	public Credential credential(Statement statement, Statement... statements) {
		List<Statement> list = new ArrayList<>(statements.length + 1);
		list.add(statement);
		list.addAll(Arrays.asList(statements));
		return credential(list);
	}

	public Credential credential(Collection<Statement> statements) {
		return credential(CREDENTIAL_EXPIRE, statements);
	}

	public Credential credential(Duration timeout, Collection<Statement> statements) {
		AliStsCredentialRequest request = new AliStsCredentialRequest();
		request.setTimeout(timeout.toSeconds());
		request.setStatements(statements);
		request.setRoleArn(properties.getRoleArn());
		request.setRoleSessionName(properties.getRoleSessionName());
		HttpResponse response = call(request);
		AliStsCredentialResponse convert = response.convert(AliStsCredentialResponse.class);
		String ak = convert.getAccessKeyId();
		String sk = convert.getAccessKeySecret();
		String token = convert.getSecurityToken();
		LocalDateTime expire = AliUtils.parse(convert.getExpire());
		return new Credential(ak, sk, token, expire);
	}

	@Override
	protected HttpResponse checkout(AliRequest request, HttpResponse response) {
		if (!response.is2xx()) {
			String string = response.string();
			log.error("AliSts call error! uri: {}; code: {}; body:\n{}", response.uri(), response.code(), string);
			throw new AliStsException("request error! code: " + response.code());
		}
		return response;
	}

}
