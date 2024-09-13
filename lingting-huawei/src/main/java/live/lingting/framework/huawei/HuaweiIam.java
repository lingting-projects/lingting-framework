package live.lingting.framework.huawei;

import live.lingting.framework.http.HttpClient;
import live.lingting.framework.http.HttpResponse;
import live.lingting.framework.huawei.exception.HuaweiIamException;
import live.lingting.framework.huawei.iam.HuaweiIamRequest;
import live.lingting.framework.huawei.iam.HuaweiIamToken;
import live.lingting.framework.huawei.iam.HuaweiIamTokenRequest;
import live.lingting.framework.huawei.iam.HuaweiIamTokenResponse;
import live.lingting.framework.huawei.properties.HuaweiIamProperties;
import live.lingting.framework.value.WaitValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.time.LocalDateTime;

import static live.lingting.framework.huawei.HuaweiUtils.CLIENT;
import static live.lingting.framework.huawei.HuaweiUtils.TOKEN_EARLY_EXPIRE;

/**
 * @author lingting 2024-09-12 21:27
 */
@Slf4j
@RequiredArgsConstructor
public class HuaweiIam {

	protected final HuaweiIamProperties properties;

	@Setter
	protected HttpClient client = CLIENT;

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

	@SneakyThrows
	protected HttpResponse call(HuaweiIamRequest iamRequest) {
		URI uri = iamRequest.urlBuilder().https().host(properties.getHost()).buildUri();
		HttpRequest.Builder builder = iamRequest.builder();
		if (iamRequest.usingToken()) {
			HuaweiIamToken token = getTokenValue().notNull();
			builder.header("X-Auth-Token", token.getValue());
		}
		HttpRequest request = builder.uri(uri).build();
		HttpResponse response = client.request(request);

		if (response.code() == 401) {
			log.debug("HuaweiIam token expired!");
			refreshToken(true);
			return call(iamRequest);
		}

		String body = response.string();
		if (!response.is2xx()) {
			log.error("HuaweiIam request error! uri: {}; code: {}; body:\n{}", request.uri(), response.code(), body);
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

}
