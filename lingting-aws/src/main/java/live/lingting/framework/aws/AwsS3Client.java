package live.lingting.framework.aws;

import live.lingting.framework.aws.exception.AwsS3Exception;
import live.lingting.framework.aws.policy.Acl;
import live.lingting.framework.aws.s3.AwsS3Properties;
import live.lingting.framework.aws.s3.AwsS3Request;
import live.lingting.framework.aws.s3.AwsS3SingV4;
import live.lingting.framework.aws.s3.AwsS3Utils;
import live.lingting.framework.http.HttpResponse;
import live.lingting.framework.http.api.ApiClient;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.util.StringUtils;
import live.lingting.framework.value.multi.StringMultiValue;
import lombok.Setter;

import java.net.http.HttpRequest;
import java.time.LocalDateTime;
import java.util.function.BiConsumer;

import static live.lingting.framework.aws.s3.AwsS3SingV4.DATETIME_FORMATTER;
import static live.lingting.framework.aws.s3.AwsS3Utils.HEADER_ACL;
import static live.lingting.framework.aws.s3.AwsS3Utils.HEADER_CONTENT_SHA256;
import static live.lingting.framework.aws.s3.AwsS3Utils.HEADER_DATE;
import static live.lingting.framework.aws.s3.AwsS3Utils.HEADER_TOKEN;
import static live.lingting.framework.aws.s3.AwsS3Utils.PAYLOAD_UNSIGNED;

/**
 * @author lingting 2024-09-19 15:02
 */
public abstract class AwsS3Client extends ApiClient<AwsS3Request> {

	protected final AwsS3Properties properties;

	protected final String ak;

	protected final String sk;

	protected final String token;

	protected final Acl acl;

	protected final String bucket;

	@Setter
	protected BiConsumer<AwsS3Request, HttpResponse> onFailed = (awsS3Request, response) -> {
		String string = response.string();
		log.error("AwsS3 call error! uri: {}; code: {}; body:\n{}", response.uri(), response.code(), string);
		throw new AwsS3Exception("request error! code: " + response.code());
	};

	protected AwsS3Client(AwsS3Properties properties) {
		super(properties.host());
		this.properties = properties;
		this.ak = properties.getAk();
		this.sk = properties.getSk();
		this.token = properties.getToken();
		this.acl = properties.getAcl();
		this.bucket = properties.getBucket();
	}

	@Override
	protected void customize(AwsS3Request request, HttpHeaders headers, HttpRequest.BodyPublisher publisher,
			StringMultiValue params) {
		if (request.getAcl() != null) {
			headers.put(HEADER_ACL, request.getAcl().getValue());
		}

		LocalDateTime now = LocalDateTime.now();
		String date = AwsS3Utils.format(now, DATETIME_FORMATTER);
		headers.put(HEADER_DATE, date);
		headers.put(HEADER_CONTENT_SHA256, PAYLOAD_UNSIGNED);

		if (StringUtils.hasText(token)) {
			headers.put(HEADER_TOKEN, token);
		}
		AwsS3SingV4 sing = AwsS3SingV4.builder()
			.dateTime(now)
			.method(request.method())
			.path(request.path())
			.headers(headers)
			.bodySha256(PAYLOAD_UNSIGNED)
			.params(params)
			.region(properties.getRegion())
			.ak(ak)
			.sk(sk)
			.bucket(bucket)
			.build();

		String authorization = sing.calculate();
		headers.authorization(authorization);
	}

	@Override
	protected HttpResponse checkout(AwsS3Request request, HttpResponse response) {
		if (!response.is2xx()) {
			onFailed.accept(request, response);
		}
		return response;
	}

}
