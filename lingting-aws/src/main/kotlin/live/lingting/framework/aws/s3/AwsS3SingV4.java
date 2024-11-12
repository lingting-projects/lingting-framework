package live.lingting.framework.aws.s3;

import live.lingting.framework.crypto.mac.Mac;
import live.lingting.framework.http.HttpMethod;
import live.lingting.framework.http.HttpRequest;
import live.lingting.framework.http.body.BodySource;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.util.ArrayUtils;
import live.lingting.framework.util.CollectionUtils;
import live.lingting.framework.util.DigestUtils;
import live.lingting.framework.util.StringUtils;
import live.lingting.framework.value.multi.StringMultiValue;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.function.BiConsumer;

import static live.lingting.framework.aws.s3.AwsS3Utils.HEADER_DATE;
import static live.lingting.framework.aws.s3.AwsS3Utils.HEADER_PREFIX;
import static live.lingting.framework.aws.s3.AwsS3Utils.PAYLOAD_UNSIGNED;
import static live.lingting.framework.util.StringUtils.deleteLast;

/**
 * @author lingting 2024-09-19 17:01
 */
public class AwsS3SingV4 {

	public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

	protected static final String[] HEADER_INCLUDE = {"host", "content-md5", "range",};

	public static final String ALGORITHM = "AWS4-HMAC-SHA256";

	public static final String SCOPE_SUFFIX = "aws4_request";

	protected final LocalDateTime dateTime;

	protected final String method;

	protected final String path;

	protected final HttpHeaders headers;

	protected final String bodySha256;

	protected final StringMultiValue params;

	protected final String region;

	protected final String ak;

	protected final String sk;

	protected final String bucket;

	public AwsS3SingV4(LocalDateTime dateTime, String method, String path, HttpHeaders headers, String bodySha256, StringMultiValue params, String region, String ak, String sk, String bucket) {
		this.dateTime = dateTime;
		this.method = method;
		this.path = path;
		this.headers = headers;
		this.bodySha256 = bodySha256;
		this.params = params;
		this.region = region;
		this.ak = ak;
		this.sk = sk;
		this.bucket = bucket;
	}

	public static S3SingV4Builder builder() {
		return new S3SingV4Builder();
	}

	public static class S3SingV4Builder {

		private LocalDateTime dateTime;

		private String method;

		private String path;

		private HttpHeaders headers;

		private String bodySha256;

		private StringMultiValue params;

		private String region;

		private String ak;

		private String sk;

		private String bucket;

		S3SingV4Builder() {
		}

		public S3SingV4Builder dateTime(LocalDateTime dateTime) {
			this.dateTime = dateTime;
			return this;
		}

		public S3SingV4Builder method(HttpMethod method) {
			return method(method.name());
		}

		public S3SingV4Builder method(String method) {
			this.method = method.toUpperCase();
			return this;
		}

		public S3SingV4Builder path(String path) {
			this.path = path;
			return this;
		}

		public S3SingV4Builder headers(HttpHeaders headers) {
			this.headers = headers;
			return this;
		}

		public S3SingV4Builder bodyUnsigned() throws NoSuchAlgorithmException {
			return body(PAYLOAD_UNSIGNED);
		}

		public S3SingV4Builder body(HttpRequest.Body body) throws NoSuchAlgorithmException {
			return body(body.string());
		}

		public S3SingV4Builder body(BodySource body) throws NoSuchAlgorithmException {
			return body(body.string());
		}

		public S3SingV4Builder body(String body) throws NoSuchAlgorithmException {
			if (PAYLOAD_UNSIGNED.equals(body)) {
				return bodySha256(PAYLOAD_UNSIGNED);
			}
			String hex = DigestUtils.sha256Hex(body);
			return bodySha256(hex);
		}

		public S3SingV4Builder bodySha256(String bodySha256) {
			this.bodySha256 = bodySha256;
			return this;
		}

		public S3SingV4Builder params(StringMultiValue params) {
			this.params = params;
			return this;
		}

		public S3SingV4Builder region(String region) {
			this.region = region;
			return this;
		}

		public S3SingV4Builder ak(String ak) {
			this.ak = ak;
			return this;
		}

		public S3SingV4Builder sk(String sk) {
			this.sk = sk;
			return this;
		}

		public S3SingV4Builder bucket(String bucket) {
			this.bucket = bucket;
			return this;
		}

		public AwsS3SingV4 build() {
			LocalDateTime time = this.dateTime == null ? LocalDateTime.now() : this.dateTime;
			return new AwsS3SingV4(time, this.method, this.path, this.headers, this.bodySha256, this.params,
				this.region, this.ak, this.sk, this.bucket);
		}

	}

	public String date() {
		return AwsS3Utils.format(dateTime, DATE_FORMATTER);
	}

	public String canonicalUri() {
		StringBuilder builder = new StringBuilder("/");

		if (StringUtils.hasText(path)) {
			builder.append(path, path.startsWith("/") ? 1 : 0, path.length());
		}

		return builder.toString();
	}

	public String canonicalQuery() {
		StringBuilder builder = new StringBuilder();
		if (params != null && !params.isEmpty()) {
			params.forEachSorted((k, vs) -> {
				String name = AwsS3Utils.encode(k);
				if (CollectionUtils.isEmpty(vs)) {
					builder.append(name).append("=").append("&");
					return;
				}
				vs.stream().sorted().forEach(v -> {
					String value = AwsS3Utils.encode(v);
					builder.append(name).append("=").append(value).append("&");
				});
			});
		}
		return deleteLast(builder).toString();
	}

	public void headersForEach(BiConsumer<String, Collection<String>> consumer) {
		headers.forEachSorted((k, vs) -> {
			if (!k.startsWith(HEADER_PREFIX) && !ArrayUtils.contains(HEADER_INCLUDE, k)) {
				return;
			}
			consumer.accept(k, vs);
		});
	}

	public String canonicalHeaders() {
		StringBuilder builder = new StringBuilder();

		headersForEach((k, vs) -> {
			for (String v : vs) {
				builder.append(k).append(":").append(v.trim()).append("\n");
			}
		});

		return builder.toString();
	}

	public String signedHeaders() {
		StringBuilder builder = new StringBuilder();

		headersForEach((k, vs) -> builder.append(k).append(";"));

		return deleteLast(builder).toString();
	}

	public String canonicalRequest() {
		String uri = canonicalUri();
		String query = canonicalQuery();
		String canonicalHeaders = canonicalHeaders();
		String signedHeaders = signedHeaders();

		return canonicalRequest(uri, query, canonicalHeaders, signedHeaders);
	}

	public String canonicalRequest(String uri, String query, String canonicalHeaders, String signedHeaders) {
		return method + "\n" + uri + "\n" + query + "\n" + canonicalHeaders + "\n" + signedHeaders + "\n" + bodySha256;
	}

	public String scope(String date) {
		return date + "/" + region + "/s3/" + SCOPE_SUFFIX;
	}

	public String source(String date, String scope, String request) throws NoSuchAlgorithmException {
		String requestSha = DigestUtils.sha256Hex(request);
		return ALGORITHM + "\n" + date + "\n" + scope + "\n" + requestSha;
	}

	public String sourceHmacSha(String source, String scopeDate)
		throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException {
		Mac mac = Mac.hmacBuilder().sha256().secret("AWS4" + sk).build();
		byte[] sourceKey1 = mac.calculate(scopeDate);
		byte[] sourceKey2 = mac.useSecret(sourceKey1).calculate(region);
		byte[] sourceKey3 = mac.useSecret(sourceKey2).calculate("s3");
		byte[] sourceKey4 = mac.useSecret(sourceKey3).calculate(SCOPE_SUFFIX);

		return mac.useSecret(sourceKey4).calculateHex(source);
	}

	/**
	 * 计算前面
	 */

	public String calculate() {
		String request = canonicalRequest();

		String scopeDate = date();
		String scope = scope(scopeDate);

		String date = headers.first(HEADER_DATE);
		String source = source(date, scope, request);
		String sourceHmacSha = sourceHmacSha(source, scopeDate);

		String signedHeaders = signedHeaders();
		return ALGORITHM + " Credential=" + ak + "/" + scope + "," + "SignedHeaders=" + signedHeaders + ","
			+ "Signature=" + sourceHmacSha;
	}

}
