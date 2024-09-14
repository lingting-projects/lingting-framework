package live.lingting.framework.ali;

import live.lingting.framework.ali.properties.AliProperties;
import live.lingting.framework.crypto.mac.Mac;
import live.lingting.framework.http.HttpUrlBuilder;
import live.lingting.framework.http.api.ApiClient;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.http.java.JavaHttpUtils;
import live.lingting.framework.util.ArrayUtils;
import live.lingting.framework.util.DigestUtils;
import live.lingting.framework.util.StringUtils;
import lombok.SneakyThrows;

import java.net.http.HttpRequest;
import java.time.LocalDateTime;

import static live.lingting.framework.util.HttpUtils.HEADER_HOST;

/**
 * @author lingting 2024-09-14 13:49
 */
public abstract class AliClient extends ApiClient<AliRequest> {

	public static final String HEADER_PREFIX = "x-acs";

	public static final String HEADER_OSS_PREFIX = "x-oss";

	public static final String ALGORITHM = "ACS3-HMAC-SHA256";

	protected static final String[] HEADER_INCLUDE = { HEADER_HOST, "content-type", "content-md5" };

	protected final String ak;

	protected final String sk;

	protected final String token;

	protected AliClient(AliProperties properties) {
		super(properties.host());
		this.ak = properties.getAk();
		this.sk = properties.getSk();
		this.token = properties.getToken();
	}

	@Override
	protected void configure(AliRequest request, HttpHeaders headers) {
		String name = request.name();
		String version = request.version();
		String nonce = request.nonce();

		headers.put("x-acs-action", name);
		headers.put("x-acs-version", version);
		headers.put("x-acs-signature-nonce", nonce);

		if (StringUtils.hasText(token)) {
			headers.put("x-acs-content-sha256", token);
		}

	}

	@SneakyThrows
	@Override
	protected void configure(AliRequest request, HttpHeaders headers, HttpRequest.Builder builder,
			HttpUrlBuilder urlBuilder) {
		String date = AliUtils.format(LocalDateTime.now());
		headers.put("x-acs-date", date);

		String method = request.method().name().toUpperCase();
		String path = urlBuilder.buildPath();
		String uri = StringUtils.hasText(path) ? path : "/";
		String query = urlBuilder.buildQuery();
		HttpRequest.BodyPublisher publisher = request.body();
		String body = publisher == null ? "" : JavaHttpUtils.toString(publisher);
		String bodySha = DigestUtils.sha256Hex(body);
		headers.put("x-acs-content-sha256", bodySha);

		StringBuilder headerBuilder = new StringBuilder();
		StringBuilder signHeaderBuilder = new StringBuilder();
		headers.forEachSorted((k, vs) -> {
			boolean isPrefix = k.startsWith(HEADER_PREFIX) || k.startsWith(HEADER_OSS_PREFIX);
			if (!isPrefix && !ArrayUtils.containsIgnoreCase(HEADER_INCLUDE, k)) {
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

}
