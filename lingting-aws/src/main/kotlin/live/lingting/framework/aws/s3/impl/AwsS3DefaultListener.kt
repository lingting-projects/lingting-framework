package live.lingting.framework.aws.s3.impl;

import live.lingting.framework.aws.AwsS3Client;
import live.lingting.framework.aws.exception.AwsS3Exception;
import live.lingting.framework.aws.s3.AwsS3Request;
import live.lingting.framework.aws.s3.AwsS3SingV4;
import live.lingting.framework.aws.s3.AwsS3Utils;
import live.lingting.framework.aws.s3.interfaces.AwsS3Listener;
import live.lingting.framework.http.HttpResponse;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.value.multi.StringMultiValue;
import org.slf4j.Logger;

import java.time.LocalDateTime;

import static live.lingting.framework.aws.s3.AwsS3SingV4.DATETIME_FORMATTER;
import static live.lingting.framework.aws.s3.AwsS3Utils.HEADER_DATE;
import static live.lingting.framework.aws.s3.AwsS3Utils.PAYLOAD_UNSIGNED;

/**
 * @author lingting 2024/11/5 14:48
 */
public class AwsS3DefaultListener implements AwsS3Listener {

	protected final AwsS3Client client;

	protected final Logger log;

	protected final String region;

	public AwsS3DefaultListener(AwsS3Client client) {
		this.client = client;
		this.log = client.getLog();
		this.region = client.getProperties().getRegion();
	}

	@Override
	public void onFailed(AwsS3Request request, HttpResponse response) {
		String string = response.string();
		log.error("Call error! uri: {}; code: {}; body:\n{}", response.uri(), response.code(), string);
		throw new AwsS3Exception("request error! code: " + response.code());
	}

	@Override
	public void onAuthorization(AwsS3Request request, HttpHeaders headers, StringMultiValue params, LocalDateTime now) {
		String date = AwsS3Utils.format(now, DATETIME_FORMATTER);
		headers.put(HEADER_DATE, date);

		AwsS3SingV4 sing = AwsS3SingV4.builder()
			.dateTime(now)
			.method(request.method())
			.path(request.path())
			.headers(headers)
			.bodySha256(PAYLOAD_UNSIGNED)
			.params(params)
			.region(region)
			.ak(client.getAk())
			.sk(client.getSk())
			.bucket(client.getBucket())
			.build();

		String authorization = sing.calculate();
		headers.authorization(authorization);
	}

}
