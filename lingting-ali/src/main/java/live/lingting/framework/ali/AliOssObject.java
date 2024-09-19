package live.lingting.framework.ali;

import com.fasterxml.jackson.databind.JsonNode;
import live.lingting.framework.ali.multipart.AliMultipartTask;
import live.lingting.framework.ali.oss.AliOssEmptyRequest;
import live.lingting.framework.ali.oss.AliOssHeaders;
import live.lingting.framework.ali.oss.AliOssMultipartMergeRequest;
import live.lingting.framework.ali.oss.AliOssObjectPutRequest;
import live.lingting.framework.ali.oss.AliOssRequest;
import live.lingting.framework.ali.properties.AliOssProperties;
import live.lingting.framework.http.HttpMethod;
import live.lingting.framework.http.HttpResponse;
import live.lingting.framework.http.HttpUrlBuilder;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.jackson.JacksonUtils;
import live.lingting.framework.multipart.Multipart;
import live.lingting.framework.multipart.Part;
import live.lingting.framework.s3.Acl;
import live.lingting.framework.stream.CloneInputStream;
import live.lingting.framework.thread.Async;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static live.lingting.framework.ali.AliUtils.MULTIPART_MAX_PART_COUNT;
import static live.lingting.framework.ali.AliUtils.MULTIPART_MAX_PART_SIZE;
import static live.lingting.framework.ali.AliUtils.MULTIPART_MIN_PART_SIZE;
import static live.lingting.framework.http.HttpMethod.DELETE;
import static live.lingting.framework.http.HttpMethod.POST;

/**
 * @author lingting 2024-09-18 10:29
 */
@Getter
public class AliOssObject extends AliOss {

	private final String key;

	public AliOssObject(AliOssProperties properties, String key) {
		super(properties);
		this.key = key;
	}

	@Override
	protected void configure(AliOssRequest request) {
		request.setKey(key);
		request.setAclIfAbsent(properties.getAcl());
	}

	// region get

	public String publicUrl() {
		return HttpUrlBuilder.builder().https().host(host).uri(key).build();
	}

	public AliOssHeaders head() {
		AliOssEmptyRequest request = new AliOssEmptyRequest(HttpMethod.HEAD);
		HttpResponse response = call(request);
		return new AliOssHeaders(response.headers());
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
		try (in) {
			AliOssObjectPutRequest request = new AliOssObjectPutRequest();
			request.setStream(in);
			request.setAcl(acl);
			call(request);
		}
	}

	public void delete() {
		AliOssEmptyRequest request = new AliOssEmptyRequest(DELETE);
		call(request);
	}

	// endregion

	// region multipart

	public String multipartInit() {
		return multipartInit(null);
	}

	public String multipartInit(Acl acl) {
		AliOssEmptyRequest request = new AliOssEmptyRequest(POST);
		request.setAcl(acl);
		request.configure(params -> params.add("uploads"));
		HttpResponse response = call(request);
		String xml = response.string();
		JsonNode node = JacksonUtils.xmlToNode(xml);
		return node.get("UploadId").asText();
	}

	public AliMultipartTask multipart(InputStream source) throws IOException {
		return multipart(source, AliUtils.MULTIPART_DEFAULT_PART_SIZE, new Async(20));
	}

	public AliMultipartTask multipart(InputStream source, long parSize, Async async) throws IOException {
		String uploadId = multipartInit();

		Multipart multipart = Multipart.builder()
			.id(uploadId)
			.source(source)
			.partSize(parSize)
			.maxPartCount(MULTIPART_MAX_PART_COUNT)
			.maxPartSize(MULTIPART_MAX_PART_SIZE)
			.minPartSize(MULTIPART_MIN_PART_SIZE)
			.build();

		AliMultipartTask task = new AliMultipartTask(multipart, async, this);
		task.start();
		return task;
	}

	/**
	 * 上传分片
	 * @return 合并用的 etag
	 */
	public String multipartUpload(String uploadId, Part part, InputStream in) {
		AliOssObjectPutRequest request = new AliOssObjectPutRequest();
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
		AliOssMultipartMergeRequest request = new AliOssMultipartMergeRequest();
		request.setUploadId(uploadId);
		request.setMap(map);
		call(request);
	}

	public void multipartCancel(String uploadId) {
		AliOssEmptyRequest request = new AliOssEmptyRequest(DELETE);
		request.configure(params -> params.add("uploadId", uploadId));
		call(request);
	}

	// endregion

}
