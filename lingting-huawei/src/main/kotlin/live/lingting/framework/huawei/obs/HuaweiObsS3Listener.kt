package live.lingting.framework.huawei.obs;

import live.lingting.framework.aws.AwsS3Client;
import live.lingting.framework.aws.s3.AwsS3Request;
import live.lingting.framework.aws.s3.AwsS3Utils;
import live.lingting.framework.aws.s3.impl.AwsS3DefaultListener;
import live.lingting.framework.http.HttpResponse;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.huawei.HuaweiObs;
import live.lingting.framework.huawei.HuaweiUtils;
import live.lingting.framework.huawei.exception.HuaweiObsException;
import live.lingting.framework.value.multi.StringMultiValue;

import java.time.LocalDateTime;

import static live.lingting.framework.aws.s3.AwsS3Utils.PAYLOAD_UNSIGNED;
import static live.lingting.framework.huawei.HuaweiUtils.HEADER_DATE;

/**
 * @author lingting 2024/11/5 14:54
 */
public class HuaweiObsS3Listener extends AwsS3DefaultListener {

	public HuaweiObsS3Listener(AwsS3Client client) {
		super(client);
	}

	@Override
	public void onFailed(AwsS3Request request, HttpResponse response) {
		String string = response.string();
		log.error("Call error! uri: {}; code: {}; body:\n{}", response.uri(), response.code(), string);
		throw new HuaweiObsException("request error! code: " + response.code());
	}

	@Override
	public void onAuthorization(AwsS3Request request, HttpHeaders headers, StringMultiValue params, LocalDateTime now) {
		String date = HuaweiUtils.format(now);
		headers.put(HEADER_DATE, date);

		headers.keys().forEach(name -> {
			if (name.startsWith(AwsS3Utils.HEADER_PREFIX)) {
				String newName = name.replace(AwsS3Utils.HEADER_PREFIX, HuaweiObs.HEADER_PREFIX);
				headers.replace(name, newName);
			}
		});
		HuaweiObsSing sing = HuaweiObsSing.builder()
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
