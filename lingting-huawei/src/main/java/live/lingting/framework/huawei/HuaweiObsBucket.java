package live.lingting.framework.huawei;

import com.fasterxml.jackson.databind.JsonNode;
import live.lingting.framework.http.HttpResponse;
import live.lingting.framework.huawei.obs.HuaweiObsEmptyRequest;
import live.lingting.framework.huawei.properties.HuaweiObsProperties;
import live.lingting.framework.jackson.JacksonUtils;

import java.util.HashMap;
import java.util.Map;

import static live.lingting.framework.http.HttpMethod.GET;

/**
 * @author lingting 2024-09-13 14:48
 */
public class HuaweiObsBucket extends HuaweiObs {

	public HuaweiObsBucket(HuaweiObsProperties properties) {
		super(properties);
	}

	public HuaweiObsObject use(String key) {
		return new HuaweiObsObject(properties, key);
	}

	/**
	 * 列举所有未完成的分片上传
	 * @return k: key, v: uploadId
	 */
	public Map<String, String> multipartList() {
		HuaweiObsEmptyRequest request = new HuaweiObsEmptyRequest(GET);
		request.getParams().add("uploads");
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
				log.warn("HuaweiObsBucket multipartList error!", e);
			}

			return map;
		});
	}

}
