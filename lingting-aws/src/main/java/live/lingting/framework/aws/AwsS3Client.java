package live.lingting.framework.aws;

import live.lingting.framework.aws.policy.Acl;
import live.lingting.framework.aws.s3.AwsS3Properties;
import live.lingting.framework.aws.s3.AwsS3Request;
import live.lingting.framework.aws.s3.impl.AwsS3DefaultListener;
import live.lingting.framework.aws.s3.interfaces.AwsS3Listener;
import live.lingting.framework.http.HttpResponse;
import live.lingting.framework.http.api.ApiClient;
import live.lingting.framework.http.body.BodySource;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.util.StringUtils;
import live.lingting.framework.value.multi.StringMultiValue;

import java.time.LocalDateTime;

import static live.lingting.framework.aws.s3.AwsS3Utils.HEADER_ACL;
import static live.lingting.framework.aws.s3.AwsS3Utils.HEADER_CONTENT_SHA256;
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

	protected AwsS3Listener listener;

	protected AwsS3Client(AwsS3Properties properties) {
		super(properties.host());
		this.properties = properties;
		this.ak = properties.getAk();
		this.sk = properties.getSk();
		this.token = properties.getToken();
		this.acl = properties.getAcl();
		this.bucket = properties.getBucket();
		this.listener = new AwsS3DefaultListener(this);
	}

	@Override
	protected void customize(AwsS3Request request, HttpHeaders headers, BodySource source, StringMultiValue params) {
		if (request.getAcl() != null) {
			headers.put(HEADER_ACL, request.getAcl().getValue());
		}

		LocalDateTime now = LocalDateTime.now();
		headers.put(HEADER_CONTENT_SHA256, PAYLOAD_UNSIGNED);

		if (StringUtils.hasText(token)) {
			headers.put(HEADER_TOKEN, token);
		}
		listener.onAuthorization(request, headers, params, now);
	}

	@Override
	protected HttpResponse checkout(AwsS3Request request, HttpResponse response) {
		if (!response.is2xx()) {
			listener.onFailed(request, response);
		}
		return response;
	}

	public AwsS3Properties getProperties() {return this.properties;}

	public String getAk() {return this.ak;}

	public String getSk() {return this.sk;}

	public String getToken() {return this.token;}

	public Acl getAcl() {return this.acl;}

	public String getBucket() {return this.bucket;}

	public AwsS3Listener getListener() {return this.listener;}

	public void setListener(AwsS3Listener listener) {this.listener = listener;}
}
