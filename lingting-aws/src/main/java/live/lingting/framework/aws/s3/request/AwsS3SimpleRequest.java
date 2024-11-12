package live.lingting.framework.aws.s3.request;

import live.lingting.framework.aws.s3.AwsS3Request;
import live.lingting.framework.http.HttpMethod;

/**
 * @author lingting 2024-09-19 19:22
 */
public class AwsS3SimpleRequest extends AwsS3Request {

	protected final HttpMethod method;

	public AwsS3SimpleRequest(HttpMethod method) {
		this.method = method;
	}

	@Override
	public HttpMethod method() {
		return method;
	}

}
