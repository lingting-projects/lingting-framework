package live.lingting.framework.huawei.obs;

import live.lingting.framework.http.HttpMethod;
import live.lingting.framework.multipart.Part;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;
import java.net.http.HttpRequest;

/**
 * @author lingting 2024-09-13 16:31
 */
@Getter
public class HuaweiObsObjectPutRequest extends HuaweiObsRequest {

	@Setter
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
	public HttpRequest.BodyPublisher body() {
		return HttpRequest.BodyPublishers.ofInputStream(this::getStream);
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

}
