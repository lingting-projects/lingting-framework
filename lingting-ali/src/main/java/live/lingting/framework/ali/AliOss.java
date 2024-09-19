package live.lingting.framework.ali;

import live.lingting.framework.ali.exception.AliOssException;
import live.lingting.framework.aws.AwsS3Client;
import live.lingting.framework.aws.s3.AwsS3Request;
import live.lingting.framework.aws.s3.interfaces.AwsS3Delegation;
import live.lingting.framework.http.HttpResponse;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.util.StringUtils;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;

import static live.lingting.framework.ali.AliUtils.HEADER_EC;
import static live.lingting.framework.ali.AliUtils.HEADER_ERR;

/**
 * @author lingting 2024-09-19 22:05
 */
public abstract class AliOss<C extends AwsS3Client> implements AwsS3Delegation<C> {

	protected final Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

	protected final C client;

	protected AliOss(C client) {
		this.client = client;
		client.setOnFailed(this::onFailed);
	}

	@Override
	public C delegation() {
		return client;
	}

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
