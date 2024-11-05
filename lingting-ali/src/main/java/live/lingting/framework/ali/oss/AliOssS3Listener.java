package live.lingting.framework.ali.oss;

import live.lingting.framework.ali.exception.AliOssException;
import live.lingting.framework.aws.AwsS3Client;
import live.lingting.framework.aws.s3.AwsS3Request;
import live.lingting.framework.aws.s3.impl.AwsS3DefaultListener;
import live.lingting.framework.http.HttpResponse;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.util.StringUtils;

import java.nio.charset.StandardCharsets;

import static live.lingting.framework.ali.AliUtils.HEADER_EC;
import static live.lingting.framework.ali.AliUtils.HEADER_ERR;

/**
 * @author lingting 2024/11/5 14:53
 */
public class AliOssS3Listener extends AwsS3DefaultListener {

	public AliOssS3Listener(AwsS3Client client) {
		super(client);
	}

	@Override
	public void onFailed(AwsS3Request request, HttpResponse response) {
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

}
