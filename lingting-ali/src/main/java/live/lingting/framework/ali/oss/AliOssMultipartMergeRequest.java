package live.lingting.framework.ali.oss;

import live.lingting.framework.http.HttpUrlBuilder;
import live.lingting.framework.multipart.Part;
import lombok.Getter;
import lombok.Setter;

import java.net.http.HttpRequest;
import java.util.Comparator;
import java.util.Map;

/**
 * @author lingting 2024-09-13 16:54
 */
@Getter
@Setter
public class AliOssMultipartMergeRequest extends AliOssRequest {

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

		map.keySet().stream().sorted(Comparator.comparing(Part::getIndex)).forEach(p -> {
			String e = map.get(p);
			builder.append("<Part><PartNumber>")
				.append(p.getIndex() + 1)
				.append("</PartNumber><ETag>")
				.append(e)
				.append("</ETag></Part>\n");
		});

		builder.append("</CompleteMultipartUpload>");
		return HttpRequest.BodyPublishers.ofString(builder.toString());
	}

}
