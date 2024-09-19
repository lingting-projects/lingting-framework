package live.lingting.framework.ali;

import com.fasterxml.jackson.databind.JsonNode;
import live.lingting.framework.ali.oss.AliOssEmptyRequest;
import live.lingting.framework.ali.properties.AliOssProperties;
import live.lingting.framework.http.HttpResponse;
import live.lingting.framework.jackson.JacksonUtils;

import java.util.HashMap;
import java.util.Map;

import static live.lingting.framework.http.HttpMethod.GET;

/**
 * @author lingting 2024-09-18 10:29
 */
public class AliOssBucket extends AliOss {

	public AliOssBucket(AliOssProperties properties) {
		super(properties);
	}

	public AliOssObject ossObject(String key) {
		return new AliOssObject(properties, key);
	}

	/**
	 * 列举所有未完成的分片上传
	 * @return k: key, v: uploadId
	 */
	public Map<String, String> multipartList() {
		AliOssEmptyRequest request = new AliOssEmptyRequest(GET);
		request.configure(params -> params.add("uploads"));
		HttpResponse response = call(request);
		return response.convert(xml -> {
			Map<String, String> map = new HashMap<>();
			try {
				JsonNode node = JacksonUtils.xmlToNode(xml);
				JsonNode tree = node.get("Upload");
				if (tree == null) {
					return map;
				}
				if (tree.isArray() && !tree.isEmpty()) {
					tree.forEach(it -> {
						String key = it.get("Key").asText();
						String uploadId = it.get("UploadId").asText();
						map.put(key, uploadId);
					});
				}
				else if (tree.isObject()) {
					String key = tree.get("Key").asText();
					String uploadId = tree.get("UploadId").asText();
					map.put(key, uploadId);
				}
			}
			catch (Exception e) {
				log.warn("AliOssBucket multipartList error!", e);
			}

			return map;
		});
	}

}
