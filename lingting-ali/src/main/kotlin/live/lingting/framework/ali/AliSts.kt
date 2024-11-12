package live.lingting.framework.ali;

import live.lingting.framework.ali.exception.AliStsException;
import live.lingting.framework.ali.properties.AliOssProperties;
import live.lingting.framework.ali.properties.AliStsProperties;
import live.lingting.framework.ali.sts.AliStsCredentialRequest;
import live.lingting.framework.ali.sts.AliStsCredentialResponse;
import live.lingting.framework.ali.sts.AliStsRequest;
import live.lingting.framework.aws.policy.Credential;
import live.lingting.framework.aws.policy.Statement;
import live.lingting.framework.crypto.mac.Mac;
import live.lingting.framework.http.HttpResponse;
import live.lingting.framework.http.HttpUrlBuilder;
import live.lingting.framework.http.body.BodySource;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.time.DatePattern;
import live.lingting.framework.util.ArrayUtils;
import live.lingting.framework.util.DigestUtils;
import live.lingting.framework.util.StringUtils;
import live.lingting.framework.value.multi.StringMultiValue;

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
public class AliSts extends AliClient<AliStsRequest> {

	public static final String ALGORITHM = "ACS3-HMAC-SHA256";

	public static final String HEADER_PREFIX = "x-acs";

	protected final AliStsProperties properties;

	public AliSts(AliStsProperties properties) {
		super(properties);
		this.properties = properties;
	}

	@Override
	protected void customize(AliStsRequest request, HttpHeaders headers) {
		String name = request.name();
		String version = request.version();
		String nonce = request.nonce();

		headers.put("x-acs-action", name);
		headers.put("x-acs-version", version);
		headers.put("x-acs-signature-nonce", nonce);

		if (StringUtils.hasText(token)) {
			headers.put("x-acs-security-token", token);
		}
	}

	@Override
	protected HttpResponse checkout(AliStsRequest request, HttpResponse response) {
		if (!response.is2xx()) {
			String string = response.string();
			log.error("AliSts call error! uri: {}; code: {}; body:\n{}", response.uri(), response.code(), string);
			throw new AliStsException("request error! code: " + response.code());
		}
		return response;
	}


	@Override
	protected void customize(AliStsRequest request, HttpHeaders headers, BodySource requestBody,
			StringMultiValue params) {
		LocalDateTime now = LocalDateTime.now();
		String date = AliUtils.format(now, DatePattern.FORMATTER_ISO_8601);
		headers.put("x-acs-date", date);

		String method = request.method().name();
		String path = request.path();
		String uri = StringUtils.hasText(path) ? path : "/";
		String query = HttpUrlBuilder.buildQuery(params);
		String body = requestBody == null ? "" : requestBody.string();
		String bodySha = DigestUtils.sha256Hex(body);

		headers.put("x-acs-content-sha256", bodySha);

		StringBuilder headerBuilder = new StringBuilder();
		StringBuilder signHeaderBuilder = new StringBuilder();
		headers.forEachSorted((k, vs) -> {
			if (!k.startsWith(HEADER_PREFIX) && !ArrayUtils.containsIgnoreCase(HEADER_INCLUDE, k)) {
				return;
			}

			for (String v : vs) {
				headerBuilder.append(k).append(":").append(v).append("\n");
			}
			signHeaderBuilder.append(k).append(";");
		});
		if (!signHeaderBuilder.isEmpty()) {
			signHeaderBuilder.deleteCharAt(signHeaderBuilder.length() - 1);
		}
		String header = headerBuilder.toString();
		String signHeader = signHeaderBuilder.toString();

		String requestString = method + "\n" + uri + "\n" + query + "\n" + header + "\n" + signHeader + "\n" + bodySha;
		String requestSha = DigestUtils.sha256Hex(requestString);

		String source = ALGORITHM + "\n" + requestSha;
		String sourceHmacSha = Mac.hmacBuilder().sha256().secret(sk).build().calculateHex(source);

		String authorization = ALGORITHM + " Credential=" + ak + ",SignedHeaders=" + signHeader + ",Signature="
				+ sourceHmacSha;

		headers.authorization(authorization);
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

	// region oss

	public AliOssBucket ossBucket(String region, String bucket) {
		AliOssProperties s = new AliOssProperties();
		s.setRegion(region);
		s.setBucket(bucket);
		return ossBucket(s);
	}

	public AliOssBucket ossBucket(String region, String bucket, Collection<String> actions) {
		AliOssProperties s = new AliOssProperties();
		s.setRegion(region);
		s.setBucket(bucket);
		return ossBucket(s, actions);
	}

	public AliOssBucket ossBucket(AliOssProperties properties) {
		return ossBucket(properties, AliActions.OSS_BUCKET_DEFAULT);
	}

	public AliOssBucket ossBucket(AliOssProperties properties, Collection<String> actions) {
		String bucket = StringUtils.hasText(properties.getBucket()) ? properties.getBucket() : "*";
		Statement statement = Statement.allow();
		statement.addAction(actions);
		statement.addResource("acs:oss:*:*:%s".formatted(bucket));
		statement.addResource("acs:oss:*:*:%s/*".formatted(bucket));
		Credential credential = credential(statement);
		AliOssProperties copy = properties.copy();
		copy.useCredential(credential);
		return new AliOssBucket(copy);
	}

	public AliOssObject ossObject(String region, String bucket, String key) {
		AliOssProperties s = new AliOssProperties();
		s.setRegion(region);
		s.setBucket(bucket);
		return ossObject(s, key);
	}

	public AliOssObject ossObject(String region, String bucket, String key, Collection<String> actions) {
		AliOssProperties s = new AliOssProperties();
		s.setRegion(region);
		s.setBucket(bucket);
		return ossObject(s, key, actions);
	}

	public AliOssObject ossObject(AliOssProperties properties, String key) {
		return ossObject(properties, key, AliActions.OSS_OBJECT_DEFAULT);
	}

	public AliOssObject ossObject(AliOssProperties properties, String key, Collection<String> actions) {
		String bucket = properties.getBucket();
		Statement statement = Statement.allow();
		statement.addAction(actions);
		statement.addResource("acs:oss:*:*:%s/%s".formatted(bucket, key));
		Credential credential = credential(statement);
		AliOssProperties copy = properties.copy();
		copy.useCredential(credential);
		return new AliOssObject(copy, key);
	}

	// endregion

}
