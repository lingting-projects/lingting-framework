package live.lingting.framework.huawei.obs;

import live.lingting.framework.crypto.mac.Mac;
import live.lingting.framework.http.HttpMethod;
import live.lingting.framework.http.HttpRequest;
import live.lingting.framework.http.HttpUrlBuilder;
import live.lingting.framework.http.body.BodySource;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.huawei.HuaweiUtils;
import live.lingting.framework.util.DigestUtils;
import live.lingting.framework.util.StringUtils;
import live.lingting.framework.value.multi.StringMultiValue;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Collection;

import static live.lingting.framework.aws.s3.AwsS3Utils.PAYLOAD_UNSIGNED;
import static live.lingting.framework.huawei.HuaweiObs.HEADER_PREFIX;
import static live.lingting.framework.huawei.HuaweiUtils.CHARSET;

/**
 * @author lingting 2024/11/5 11:18
 */
@RequiredArgsConstructor
public class HuaweiObsSing {

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

	public static HuaweiObsSingBuilder builder() {
		return new HuaweiObsSingBuilder();
	}

	public static class HuaweiObsSingBuilder {

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

		public HuaweiObsSingBuilder dateTime(LocalDateTime dateTime) {
			this.dateTime = dateTime;
			return this;
		}

		public HuaweiObsSingBuilder method(HttpMethod method) {
			return method(method.name());
		}

		public HuaweiObsSingBuilder method(String method) {
			this.method = method.toUpperCase();
			return this;
		}

		public HuaweiObsSingBuilder path(String path) {
			this.path = path;
			return this;
		}

		public HuaweiObsSingBuilder headers(HttpHeaders headers) {
			this.headers = headers;
			return this;
		}

		public HuaweiObsSingBuilder bodyUnsigned() throws NoSuchAlgorithmException {
			return body(PAYLOAD_UNSIGNED);
		}

		public HuaweiObsSingBuilder body(HttpRequest.Body body) throws NoSuchAlgorithmException {
			return body(body.string());
		}

		public HuaweiObsSingBuilder body(BodySource body) throws NoSuchAlgorithmException {
			return body(body.string());
		}

		public HuaweiObsSingBuilder body(String body) throws NoSuchAlgorithmException {
			if (PAYLOAD_UNSIGNED.equals(body)) {
				return bodySha256(PAYLOAD_UNSIGNED);
			}
			String hex = DigestUtils.sha256Hex(body);
			return bodySha256(hex);
		}

		public HuaweiObsSingBuilder bodySha256(String bodySha256) {
			this.bodySha256 = bodySha256;
			return this;
		}

		public HuaweiObsSingBuilder params(StringMultiValue params) {
			this.params = params;
			return this;
		}

		public HuaweiObsSingBuilder region(String region) {
			this.region = region;
			return this;
		}

		public HuaweiObsSingBuilder ak(String ak) {
			this.ak = ak;
			return this;
		}

		public HuaweiObsSingBuilder sk(String sk) {
			this.sk = sk;
			return this;
		}

		public HuaweiObsSingBuilder bucket(String bucket) {
			this.bucket = bucket;
			return this;
		}

		public HuaweiObsSing build() {
			LocalDateTime time = this.dateTime == null ? LocalDateTime.now() : this.dateTime;
			return new HuaweiObsSing(time, this.method, this.path, this.headers, this.bodySha256, this.params,
					this.region, this.ak, this.sk, this.bucket);
		}

	}

	public String contentType() {
		String type = headers.contentType();
		return StringUtils.hasText(type) ? type : "";
	}

	public String date() {
		return HuaweiUtils.format(dateTime);
	}

	public String canonicalizedHeaders() {
		StringBuilder builder = new StringBuilder();
		headers.keys().stream().filter(k -> k.startsWith(HEADER_PREFIX)).sorted().forEach(k -> {
			Collection<String> vs = headers.get(k);
			if (vs.isEmpty()) {
				return;
			}
			builder.append(k).append(":").append(String.join(",", vs)).append("\n");
		});
		return builder.toString();
	}

	public String query() {
		return HttpUrlBuilder.buildQuery(params);
	}

	public String canonicalizedResource() {
		String query = query();
		return canonicalizedResource(query);
	}

	public String canonicalizedResource(String query) {
		StringBuilder builder = new StringBuilder();
		builder.append("/").append(bucket).append("/");
		if (StringUtils.hasText(path)) {
			builder.append(path, path.startsWith("/") ? 1 : 0, path.length());
		}

		if (StringUtils.hasText(query)) {
			builder.append("?").append(query);
		}
		return builder.toString();
	}

	public String source() {
		String md5 = "";
		String type = contentType();
		String date = date();
		String canonicalizedHeaders = canonicalizedHeaders();
		String canonicalizedResource = canonicalizedResource();
		return method + "\n" + md5 + "\n" + type + "\n" + date + "\n" + canonicalizedHeaders + canonicalizedResource;
	}

	@SneakyThrows
	public String calculate() {
		String source = source();
		Mac mac = Mac.hmacBuilder().sha1().secret(sk).charset(CHARSET).build();
		String base64 = mac.calculateBase64(source);
		return "OBS %s:%s".formatted(ak, base64);
	}

}
