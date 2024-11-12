package live.lingting.framework.aws.s3.request;

import live.lingting.framework.aws.s3.AwsS3Request;
import live.lingting.framework.http.HttpMethod;
import live.lingting.framework.http.body.BodySource;
import live.lingting.framework.multipart.Part;

import java.io.InputStream;

/**
 * @author lingting 2024-09-13 16:31
 */
public class AwsS3ObjectPutRequest extends AwsS3Request {

	protected InputStream stream;

	protected String uploadId;

	protected Part part;

	public void multipart(String id, Part part) {
		this.uploadId = id;
		this.part = part;
	}

	@Override
	public HttpMethod method() {
		return HttpMethod.PUT;
	}


	@Override
	public BodySource body() {
		InputStream inputStream = getStream();
		return BodySource.of(inputStream);
	}

	@Override
	public void onCall() {
		headers.contentType("application/octet-stream");
	}

	@Override
	public void onParams() {
		if (part != null) {
			getParams().add("partNumber", Long.toString(part.getIndex() + 1));
			getParams().add("uploadId", uploadId);
		}
	}

	public InputStream getStream() {return this.stream;}

	public String getUploadId() {return this.uploadId;}

	public Part getPart() {return this.part;}

	public void setStream(InputStream stream) {this.stream = stream;}
}
