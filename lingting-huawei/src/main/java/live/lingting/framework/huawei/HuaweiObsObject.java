package live.lingting.framework.huawei;

import com.fasterxml.jackson.databind.JsonNode;
import live.lingting.framework.http.HttpResponse;
import live.lingting.framework.http.HttpUrlBuilder;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.huawei.multipart.HuaweiMultipartTask;
import live.lingting.framework.huawei.obs.HuaweiObsEmptyRequest;
import live.lingting.framework.huawei.obs.HuaweiObsHeaders;
import live.lingting.framework.huawei.obs.HuaweiObsMultipartMergeRequest;
import live.lingting.framework.huawei.obs.HuaweiObsObjectPutRequest;
import live.lingting.framework.huawei.obs.HuaweiObsRequest;
import live.lingting.framework.huawei.properties.HuaweiObsProperties;
import live.lingting.framework.jackson.JacksonUtils;
import live.lingting.framework.multipart.Multipart;
import live.lingting.framework.multipart.Part;
import live.lingting.framework.s3.Acl;
import live.lingting.framework.stream.CloneInputStream;
import live.lingting.framework.thread.Async;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static live.lingting.framework.http.HttpMethod.DELETE;
import static live.lingting.framework.http.HttpMethod.HEAD;
import static live.lingting.framework.http.HttpMethod.POST;
import static live.lingting.framework.huawei.HuaweiUtils.MULTIPART_MAX_PART_COUNT;
import static live.lingting.framework.huawei.HuaweiUtils.MULTIPART_MAX_PART_SIZE;
import static live.lingting.framework.huawei.HuaweiUtils.MULTIPART_MIN_PART_SIZE;

/**
 * @author lingting 2024-09-13 14:48
 */
public class HuaweiObsObject extends HuaweiObs {

	protected final String key;

	public HuaweiObsObject(HuaweiObsProperties properties, String key) {
		super(properties);
		this.key = key;
	}

	@Override
	protected void configure(HuaweiObsRequest request) {
		request.setKey(key);
		request.setAclIfAbsent(properties.getAcl());
	}

	// region get

	public String publicUrl() {
		return HttpUrlBuilder.builder().https().host(host).uri(key).build();
	}

	public HuaweiObsHeaders head() {
		HuaweiObsEmptyRequest request = new HuaweiObsEmptyRequest(HEAD);
		HttpHeaders headers = call(request).headers();
		return new HuaweiObsHeaders(headers);
	}

	// endregion

	// region put

	public void put(File file) throws IOException {
		put(file, null);
	}

	public void put(File file, Acl acl) throws IOException {
		put(new CloneInputStream(file), acl);
	}

	public void put(InputStream in) throws IOException {
		put(in, null);
	}

	public void put(InputStream in, Acl acl) throws IOException {
		put(new CloneInputStream(in), acl);
	}

	public void put(CloneInputStream in) {
		put(in, null);
	}

	public void put(CloneInputStream in, Acl acl) {
		HuaweiObsObjectPutRequest request = new HuaweiObsObjectPutRequest();
		request.setStream(in);
		request.setAcl(acl);
		call(request);
	}

	public void delete() {
		HuaweiObsEmptyRequest request = new HuaweiObsEmptyRequest(DELETE);
		call(request);
	}

	// endregion

	// region multipart

	public String multipartInit() {
		return multipartInit(null);
	}

	public String multipartInit(Acl acl) {
		HuaweiObsEmptyRequest request = new HuaweiObsEmptyRequest(POST);
		request.setAcl(acl);
		request.configure(params -> params.add("uploads"));
		HttpResponse response = call(request);
		String xml = response.string();
		JsonNode node = JacksonUtils.xmlToNode(xml);
		return node.get("UploadId").asText();
	}

	public HuaweiMultipartTask multipart(InputStream source) throws IOException {
		return multipart(source, HuaweiUtils.MULTIPART_DEFAULT_PART_SIZE, new Async(20));
	}

	public HuaweiMultipartTask multipart(InputStream source, long parSize, Async async) throws IOException {
		String uploadId = multipartInit();

		Multipart multipart = Multipart.builder()
			.id(uploadId)
			.source(source)
			.partSize(parSize)
			.maxPartCount(MULTIPART_MAX_PART_COUNT)
			.maxPartSize(MULTIPART_MAX_PART_SIZE)
			.minPartSize(MULTIPART_MIN_PART_SIZE)
			.build();

		HuaweiMultipartTask task = new HuaweiMultipartTask(multipart, async, this);
		task.start();
		return task;
	}

	/**
	 * 上传分片
	 * @return 合并用的 etag
	 */
	public String multipartUpload(String uploadId, Part part, InputStream in) {
		HuaweiObsObjectPutRequest request = new HuaweiObsObjectPutRequest();
		request.setStream(in);
		request.multipart(uploadId, part);
		HttpResponse response = call(request);
		HttpHeaders headers = response.headers();
		return headers.etag();
	}

	/**
	 * 合并分片
	 * @param map key: part. value: etag
	 */
	public void multipartMerge(String uploadId, Map<Part, String> map) {
		HuaweiObsMultipartMergeRequest request = new HuaweiObsMultipartMergeRequest();
		request.setUploadId(uploadId);
		request.setMap(map);
		call(request);
	}

	public void multipartCancel(String uploadId) {
		HuaweiObsEmptyRequest request = new HuaweiObsEmptyRequest(DELETE);
		request.configure(params -> params.add("uploadId", uploadId));
		call(request);
	}

	// endregion

}
