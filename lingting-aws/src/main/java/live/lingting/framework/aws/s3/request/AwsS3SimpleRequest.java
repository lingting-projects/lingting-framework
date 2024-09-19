package live.lingting.framework.aws.s3.request;

import live.lingting.framework.aws.s3.AwsS3Request;
import live.lingting.framework.http.HttpMethod;
import lombok.RequiredArgsConstructor;

/**
 * @author lingting 2024-09-19 19:22
 */
@RequiredArgsConstructor
public class AwsS3SimpleRequest extends AwsS3Request {

	protected final HttpMethod method;

	@Override
	public HttpMethod method() {
		return method;
	}

}
