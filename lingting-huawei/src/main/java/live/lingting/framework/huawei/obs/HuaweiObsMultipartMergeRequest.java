package live.lingting.framework.huawei.obs;

import live.lingting.framework.http.HttpUrlBuilder;
import live.lingting.framework.multipart.Part;
import lombok.Getter;
import lombok.Setter;

import java.net.http.HttpRequest;
import java.util.Map;

/**
 * @author lingting 2024-09-13 16:54
 */
@Getter
@Setter
public class HuaweiObsMultipartMergeRequest extends HuaweiObsRequest {

	private String uploadId;

	private Map<Part, String> map;

	@Override
	public void configure(HttpUrlBuilder builder) {
		super.configure(builder);
		builder.addParam("uploadId", uploadId);
	}

	@Override
	public HttpRequest.BodyPublisher body() {
		StringBuilder builder = new StringBuilder("<CompleteMultipartUpload>\n");

		map.forEach((p, e) -> builder.append("<Part><PartNumber>")
			.append(p.getIndex() + 1)
			.append("</PartNumber><ETag>")
			.append(e)
			.append("</ETag></Part>\n"));

		builder.append("</CompleteMultipartUpload>");
		return HttpRequest.BodyPublishers.ofString(builder.toString());
	}

}
