package live.lingting.framework.aws.s3.request;

import live.lingting.framework.aws.s3.AwsS3Request;
import live.lingting.framework.http.HttpMethod;
import live.lingting.framework.http.body.BodySource;
import live.lingting.framework.http.body.MemoryBody;
import live.lingting.framework.multipart.Part;

import java.util.Comparator;
import java.util.Map;

/**
 * @author lingting 2024-09-13 16:54
 */
public class AwsS3MultipartMergeRequest extends AwsS3Request {

	private String uploadId;

	private Map<Part, String> map;

	@Override
	public HttpMethod method() {
		return HttpMethod.POST;
	}

	@Override
	public BodySource body() {
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
	public void onParams() {
		getParams().add("uploadId", uploadId);
	}

	public String getUploadId() {return this.uploadId;}

	public Map<Part, String> getMap() {return this.map;}

	public void setUploadId(String uploadId) {this.uploadId = uploadId;}

	public void setMap(Map<Part, String> map) {this.map = map;}
}
