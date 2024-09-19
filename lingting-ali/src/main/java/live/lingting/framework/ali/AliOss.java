package live.lingting.framework.ali;

import live.lingting.framework.ali.exception.AliOssException;
import live.lingting.framework.ali.oss.AliOssRequest;
import live.lingting.framework.ali.properties.AliOssProperties;
import live.lingting.framework.crypto.mac.Mac;
import live.lingting.framework.http.HttpResponse;
import live.lingting.framework.http.HttpUrlBuilder;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.util.ArrayUtils;
import live.lingting.framework.util.DigestUtils;
import live.lingting.framework.util.StringUtils;

import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static live.lingting.framework.ali.AliUtils.FORMATTER_ISO_8601_D;

/**
 * @author lingting 2024-09-14 20:11
 */
public abstract class AliOss extends AliClient<AliOssRequest> {

	public static final String ALGORITHM = "OSS4-HMAC-SHA256";

	protected static final String[] HEADER_EXCLUDE = { "content-type", "content-md5" };

	public static final String HEADER_PREFIX = "x-oss";

	public static final String HEADER_ERR = "x-oss-err";

	public static final String HEADER_EC = "x-oss-ec";

	protected final AliOssProperties properties;

	protected AliOss(AliOssProperties properties) {
		super(properties);
		this.properties = properties;
	}

	@Override
	protected void configure(AliOssRequest request, HttpHeaders headers) {
		if (request.getAcl() != null) {
			headers.put("x-oss-object-acl", request.getAcl().getValue());
		}
	}

	@Override
	protected HttpResponse checkout(AliOssRequest request, HttpResponse response) {
		if (!response.is2xx()) {
			HttpHeaders headers = response.headers();
			String ec = headers.first(HEADER_EC, "");

			String string = response.string();

			if (!StringUtils.hasText(string)) {
				String err = headers.first(HEADER_ERR, "");
				if (StringUtils.hasText(err)) {
					byte[] base64 = StringUtils.base64(err);
					string = new String(base64, StandardCharsets.UTF_8);
				}
			}

			log.error("AliOss call error! uri: {}; code: {}; ec: {}; body:\n{}", response.uri(), response.code(), ec,
					string);
			throw new AliOssException("request error! code: " + response.code());
		}
		return response;
	}

	@Override
	protected void authorization(AliOssRequest request, HttpHeaders headers, HttpRequest.Builder builder,
			HttpUrlBuilder urlBuilder) throws Exception {
		LocalDateTime now = LocalDateTime.now();
		String date = AliUtils.format(now, FORMATTER_ISO_8601_D);
		headers.put("x-oss-date", date);
		headers.put("x-oss-content-sha256", BODY_EMPTY);

		if (StringUtils.hasText(token)) {
			headers.put("x-oss-security-token", token);
		}

		String method = request.method().name();
		StringBuilder uriBuilder = new StringBuilder("/").append(properties.getBucket()).append("/");
		String path = urlBuilder.buildPath();
		if (StringUtils.hasText(path)) {
			uriBuilder.append(path, 1, path.length());
		}
		String uri = uriBuilder.toString();
		String query = urlBuilder.buildQuery();

		StringBuilder canonicalBuilder = new StringBuilder();
		StringBuilder additionalBuilder = new StringBuilder();
		headers.forEachSorted((k, vs) -> {
			if (!k.startsWith(HEADER_PREFIX) && !ArrayUtils.containsIgnoreCase(HEADER_INCLUDE, k)) {
				return;
			}

			for (String v : vs) {
				canonicalBuilder.append(k).append(":").append(v).append("\n");
			}
			if (!k.startsWith(HEADER_PREFIX) && !ArrayUtils.containsIgnoreCase(HEADER_EXCLUDE, k)) {
				additionalBuilder.append(k).append(";");
			}
		});
		if (!additionalBuilder.isEmpty()) {
			additionalBuilder.deleteCharAt(additionalBuilder.length() - 1);
		}

		String requestSource = method + "\n" + uri + "\n" + query + "\n" + canonicalBuilder + "\n" + additionalBuilder
				+ "\n" + BODY_EMPTY;
		String requestSha = DigestUtils.sha256Hex(requestSource);

		String scopeDate = "%d%02d%02d".formatted(now.getYear(), now.getMonthValue(), now.getDayOfMonth());
		String scope = "%s/%s/oss/aliyun_v4_request".formatted(scopeDate, properties.getRegion());
		String source = ALGORITHM + "\n" + date + "\n" + scope + "\n" + requestSha;

		Mac mac = Mac.hmacBuilder().sha256().secret("aliyun_v4" + sk).build();
		byte[] sourceKey1 = mac.calculate(scopeDate);
		byte[] sourceKey2 = mac.useSecret(sourceKey1).calculate(properties.getRegion());
		byte[] sourceKey3 = mac.useSecret(sourceKey2).calculate("oss");
		byte[] sourceKey4 = mac.useSecret(sourceKey3).calculate("aliyun_v4_request");

		String sourceHmacSha = mac.useSecret(sourceKey4).calculateHex(source);

		StringBuilder authorizationBuilder = new StringBuilder(ALGORITHM).append(" Credential=")
			.append(ak)
			.append("/")
			.append(scope)
			.append(", ");

		if (!additionalBuilder.isEmpty()) {
			authorizationBuilder.append("AdditionalHeaders=").append(additionalBuilder).append(", ");
		}

		authorizationBuilder.append("Signature=").append(sourceHmacSha);

		String authorization = authorizationBuilder.toString();
		headers.authorization(authorization);
	}

}
