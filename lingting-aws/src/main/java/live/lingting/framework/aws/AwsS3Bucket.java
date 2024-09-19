package live.lingting.framework.aws;

import com.fasterxml.jackson.databind.JsonNode;
import live.lingting.framework.aws.s3.AwsS3Properties;
import live.lingting.framework.aws.s3.request.AwsS3SimpleRequest;
import live.lingting.framework.aws.s3.response.AwsS3MultipartItem;
import live.lingting.framework.http.HttpResponse;
import live.lingting.framework.jackson.JacksonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static live.lingting.framework.http.HttpMethod.GET;

/**
 * @author lingting 2024-09-19 15:09
 */
public class AwsS3Bucket extends AwsS3Client {

	public AwsS3Bucket(AwsS3Properties properties) {
		super(properties);
	}

	public AwsS3Object s3Object(String key) {
		return new AwsS3Object(properties, key);
	}

	/**
	 * 列举所有未完成的分片上传
	 * @return k: uploadId, v: k
	 */
	public List<AwsS3MultipartItem> multipartList() {
		return multipartList(null);
	}

	public List<AwsS3MultipartItem> multipartList(Consumer<AwsS3SimpleRequest> consumer) {
		AwsS3SimpleRequest request = new AwsS3SimpleRequest(GET);
		if (consumer != null) {
			consumer.accept(request);
		}
		request.getParams().add("uploads");
		HttpResponse response = call(request);
		return response.convert(xml -> {
			List<AwsS3MultipartItem> list = new ArrayList<>();
			try {
				JsonNode node = JacksonUtils.xmlToNode(xml);
				JsonNode tree = node.get("Upload");
				if (tree == null) {
					return list;
				}
				if (tree.isArray() && !tree.isEmpty()) {
					tree.forEach(it -> {
						String key = it.get("Key").asText();
						String uploadId = it.get("UploadId").asText();
						list.add(new AwsS3MultipartItem(key, uploadId));
					});
				}
				else if (tree.isObject()) {
					String key = tree.get("Key").asText();
					String uploadId = tree.get("UploadId").asText();
					list.add(new AwsS3MultipartItem(key, uploadId));
				}
			}
			catch (Exception e) {
				log.warn("AliOssBucket multipartList error!", e);
			}

			return list;
		});
	}

}
