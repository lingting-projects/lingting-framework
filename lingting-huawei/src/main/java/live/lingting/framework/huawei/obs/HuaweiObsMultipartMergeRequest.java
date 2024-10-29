package live.lingting.framework.huawei.obs;

import live.lingting.framework.http.body.MemoryBody;
import live.lingting.framework.multipart.Part;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
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
	public MemoryBody body() {
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
		return new MemoryBody(builder.toString());
	}

	@Override
	public void onCall() {
		super.onCall();
		getParams().add("uploadId", uploadId);
	}

}
