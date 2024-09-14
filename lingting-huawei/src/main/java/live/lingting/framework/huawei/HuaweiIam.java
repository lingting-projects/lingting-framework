package live.lingting.framework.huawei;

import live.lingting.framework.http.HttpResponse;
import live.lingting.framework.http.api.ApiClient;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.huawei.exception.HuaweiIamException;
import live.lingting.framework.huawei.iam.HuaweiIamCredentialRequest;
import live.lingting.framework.huawei.iam.HuaweiIamCredentialResponse;
import live.lingting.framework.huawei.iam.HuaweiIamRequest;
import live.lingting.framework.huawei.iam.HuaweiIamToken;
import live.lingting.framework.huawei.iam.HuaweiIamTokenRequest;
import live.lingting.framework.huawei.iam.HuaweiIamTokenResponse;
import live.lingting.framework.huawei.properties.HuaweiIamProperties;
import live.lingting.framework.huawei.properties.HuaweiObsProperties;
import live.lingting.framework.s3.Credential;
import live.lingting.framework.util.StringUtils;
import live.lingting.framework.value.WaitValue;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static live.lingting.framework.huawei.HuaweiUtils.CREDENTIAL_EXPIRE;
import static live.lingting.framework.huawei.HuaweiUtils.TOKEN_EARLY_EXPIRE;

/**
 * @author lingting 2024-09-12 21:27
 */
@Slf4j
public class HuaweiIam extends ApiClient<HuaweiIamRequest> {

	protected final HuaweiIamProperties properties;

	/**
	 * token 提前多久过期
	 */
	@Getter
	@Setter
	protected Duration tokenEarlyExpire = TOKEN_EARLY_EXPIRE;

	/**
	 * token 需要外部维护
	 */
	@Getter
	protected WaitValue<HuaweiIamToken> tokenValue = WaitValue.of();

	public HuaweiIam(HuaweiIamProperties properties) {
		super(properties.getHost());
		this.properties = properties;
	}

	@SneakyThrows
	@Override
	protected void configure(HuaweiIamRequest request, HttpHeaders headers) {
		if (request.usingToken()) {
			HuaweiIamToken token = getTokenValue().notNull();
			headers.put("X-Auth-Token", token.getValue());
		}
	}

	@Override
	protected HttpResponse checkout(HuaweiIamRequest request, HttpResponse response) {
		if (response.code() == 401) {
			log.debug("HuaweiIam token expired!");
			refreshToken(true);
			return call(request);
		}

		String string = response.string();
		if (!response.is2xx()) {
			log.error("HuaweiIam request error! uri: {}; code: {}; body:\n{}", response.uri(), response.code(), string);
			throw new HuaweiIamException("request error! code: " + response.code());
		}
		return response;
	}

	public void refreshToken() {
		refreshToken(false);
	}

	public void refreshToken(boolean force) {
		if (!force) {
			HuaweiIamToken value = getTokenValue().getValue();
			if (value != null && !value.isExpired(getTokenEarlyExpire())) {
				return;
			}
		}

		HuaweiIamToken token = token();
		tokenValue.setValue(token);
	}

	public HuaweiIamToken token() {
		HuaweiIamTokenRequest request = new HuaweiIamTokenRequest();
		request.setDomain(properties.getDomain());
		request.setUsername(properties.getUsername());
		request.setPassword(properties.getPassword());

		HttpResponse response = call(request);
		HuaweiIamTokenResponse convert = response.convert(HuaweiIamTokenResponse.class);

		String token = response.headers().first("X-Subject-Token");
		LocalDateTime expire = HuaweiUtils.parse(convert.getExpire(), properties.getZone());
		LocalDateTime issued = HuaweiUtils.parse(convert.getIssued(), properties.getZone());
		return new HuaweiIamToken(token, expire, issued);
	}

	public Credential credential(HuaweiStatement statement) {
		return credential(Collections.singleton(statement));
	}

	public Credential credential(HuaweiStatement statement, HuaweiStatement... statements) {
		List<HuaweiStatement> list = new ArrayList<>(statements.length + 1);
		list.add(statement);
		list.addAll(Arrays.asList(statements));
		return credential(list);
	}

	public Credential credential(Collection<HuaweiStatement> statements) {
		return credential(CREDENTIAL_EXPIRE, statements);
	}

	public Credential credential(Duration timeout, Collection<HuaweiStatement> statements) {
		HuaweiIamCredentialRequest request = new HuaweiIamCredentialRequest();
		request.setTimeout(timeout);
		request.setStatements(statements);
		HttpResponse response = call(request);
		HuaweiIamCredentialResponse convert = response.convert(HuaweiIamCredentialResponse.class);
		String ak = convert.getAccess();
		String sk = convert.getSecret();
		String token = convert.getSecurityToken();
		LocalDateTime expire = HuaweiUtils.parse(convert.getExpire(), properties.getZone());
		return new Credential(ak, sk, token, expire);
	}

	// region obs

	public HuaweiObsBucket obsBucket(String region, String bucket) {
		HuaweiObsProperties s = new HuaweiObsProperties();
		s.setRegion(region);
		s.setBucket(bucket);
		return obsBucket(s);
	}

	public HuaweiObsBucket obsBucket(String region, String bucket, Collection<String> actions) {
		HuaweiObsProperties s = new HuaweiObsProperties();
		s.setRegion(region);
		s.setBucket(bucket);
		return obsBucket(s, actions);
	}

	public HuaweiObsBucket obsBucket(HuaweiObsProperties properties) {
		return obsBucket(properties, HuaweiActions.OBS_BUCKET_DEFAULT);
	}

	public HuaweiObsBucket obsBucket(HuaweiObsProperties properties, Collection<String> actions) {
		String bucket = StringUtils.hasText(properties.getBucket()) ? properties.getBucket() : "*";
		HuaweiStatement statement = HuaweiStatement.allow();
		statement.addAction(actions);
		statement.addResource("obs:*:*:bucket:%s".formatted(bucket));
		statement.addResource("obs:*:*:object:%s/*".formatted(bucket));
		Credential credential = credential(statement);
		HuaweiObsProperties copy = properties.copy();
		copy.useCredential(credential);
		return new HuaweiObsBucket(copy);
	}

	public HuaweiObsObject obsObject(String region, String bucket, String key) {
		HuaweiObsProperties s = new HuaweiObsProperties();
		s.setRegion(region);
		s.setBucket(bucket);
		return obsObject(s, key);
	}

	public HuaweiObsObject obsObject(String region, String bucket, String key, Collection<String> actions) {
		HuaweiObsProperties s = new HuaweiObsProperties();
		s.setRegion(region);
		s.setBucket(bucket);
		return obsObject(s, key, actions);
	}

	public HuaweiObsObject obsObject(HuaweiObsProperties properties, String key) {
		return obsObject(properties, key, HuaweiActions.OBS_OBJECT_DEFAULT);
	}

	public HuaweiObsObject obsObject(HuaweiObsProperties properties, String key, Collection<String> actions) {
		String bucket = properties.getBucket();
		HuaweiStatement statement = HuaweiStatement.allow();
		statement.addAction(actions);
		statement.addResource("obs:*:*:object:%s/%s".formatted(bucket, key));
		Credential credential = credential(statement);
		HuaweiObsProperties copy = properties.copy();
		copy.useCredential(credential);
		return new HuaweiObsObject(copy, key);
	}

	// endregion

}
