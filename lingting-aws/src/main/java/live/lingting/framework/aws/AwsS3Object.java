package live.lingting.framework.aws;

import com.fasterxml.jackson.databind.JsonNode;
import live.lingting.framework.aws.policy.Acl;
import live.lingting.framework.aws.s3.AwsS3MultipartTask;
import live.lingting.framework.aws.s3.AwsS3Properties;
import live.lingting.framework.aws.s3.AwsS3Request;
import live.lingting.framework.aws.s3.interfaces.AwsS3ObjectInterface;
import live.lingting.framework.aws.s3.request.AwsS3MultipartMergeRequest;
import live.lingting.framework.aws.s3.request.AwsS3ObjectPutRequest;
import live.lingting.framework.aws.s3.request.AwsS3SimpleRequest;
import live.lingting.framework.http.HttpMethod;
import live.lingting.framework.http.HttpResponse;
import live.lingting.framework.http.HttpUrlBuilder;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.jackson.JacksonUtils;
import live.lingting.framework.multipart.Multipart;
import live.lingting.framework.multipart.Part;
import live.lingting.framework.stream.CloneInputStream;
import live.lingting.framework.stream.FileCloneInputStream;
import live.lingting.framework.thread.Async;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static live.lingting.framework.aws.s3.AwsS3Utils.MULTIPART_DEFAULT_PART_SIZE;
import static live.lingting.framework.aws.s3.AwsS3Utils.MULTIPART_MAX_PART_COUNT;
import static live.lingting.framework.aws.s3.AwsS3Utils.MULTIPART_MAX_PART_SIZE;
import static live.lingting.framework.aws.s3.AwsS3Utils.MULTIPART_MIN_PART_SIZE;
import static live.lingting.framework.http.HttpMethod.DELETE;
import static live.lingting.framework.http.HttpMethod.POST;

/**
 * @author lingting 2024-09-19 15:09
 */
@Getter
public class AwsS3Object extends AwsS3Client implements AwsS3ObjectInterface {

	protected final String key;

	protected final String publicUrl;

	public AwsS3Object(AwsS3Properties properties, String key) {
		super(properties);
		this.key = key;
		this.publicUrl = HttpUrlBuilder.builder().https().host(host).uri(key).build();
	}

	@Override
	protected void customize(AwsS3Request request) {
		request.setKey(key);
		request.setAclIfAbsent(acl);
	}

	// region get

	@Override
	public String publicUrl() {
		return publicUrl;
	}

	@Override
	public HttpHeaders head() {
		AwsS3SimpleRequest request = new AwsS3SimpleRequest(HttpMethod.HEAD);
		HttpResponse response = call(request);
		return response.headers();
	}

	// endregion

	// region put

	@Override
	public void put(File file) throws IOException {
		put(file, null);
	}

	@Override
	public void put(File file, Acl acl) throws IOException {
		put(new FileCloneInputStream(file), acl);
	}

	@Override
	public void put(InputStream in) throws IOException {
		put(in, null);
	}

	@Override
	public void put(InputStream in, Acl acl) throws IOException {
		put(new FileCloneInputStream(in), acl);
	}

	@Override
	public void put(CloneInputStream in) {
		put(in, null);
	}

	@Override
	public void put(CloneInputStream in, Acl acl) {
		try (in) {
			AwsS3ObjectPutRequest request = new AwsS3ObjectPutRequest();
			request.setStream(in);
			request.setAcl(acl);
			call(request);
		}
	}

	@Override
	public void delete() {
		AwsS3SimpleRequest request = new AwsS3SimpleRequest(DELETE);
		call(request);
	}

	// endregion

	// region multipart

	@Override
	public String multipartInit() {
		return multipartInit(null);
	}

	@Override
	public String multipartInit(Acl acl) {
		AwsS3SimpleRequest request = new AwsS3SimpleRequest(POST);
		request.setAcl(acl);
		request.getParams().add("uploads");
		HttpResponse response = call(request);
		String xml = response.string();
		JsonNode node = JacksonUtils.xmlToNode(xml);
		return node.get("UploadId").asText();
	}

	@Override
	public AwsS3MultipartTask multipart(InputStream source) throws IOException {
		return multipart(source, MULTIPART_DEFAULT_PART_SIZE, new Async(20));
	}

	@Override
	public AwsS3MultipartTask multipart(InputStream source, long parSize, Async async) throws IOException {
		String uploadId = multipartInit();

		Multipart multipart = Multipart.builder()
			.id(uploadId)
			.source(source)
			.partSize(parSize)
			.maxPartCount(MULTIPART_MAX_PART_COUNT)
			.maxPartSize(MULTIPART_MAX_PART_SIZE)
			.minPartSize(MULTIPART_MIN_PART_SIZE)
			.build();

		AwsS3MultipartTask task = new AwsS3MultipartTask(multipart, async, this);
		task.start();
		return task;
	}

	/**
	 * 上传分片
	 * @return 合并用的 etag
	 */
	@Override
	public String multipartUpload(String uploadId, Part part, InputStream in) {
		AwsS3ObjectPutRequest request = new AwsS3ObjectPutRequest();
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
	@Override
	public void multipartMerge(String uploadId, Map<Part, String> map) {
		AwsS3MultipartMergeRequest request = new AwsS3MultipartMergeRequest();
		request.setUploadId(uploadId);
		request.setMap(map);
		call(request);
	}

	@Override
	public void multipartCancel(String uploadId) {
		AwsS3SimpleRequest request = new AwsS3SimpleRequest(DELETE);
		request.getParams().add("uploadId", uploadId);
		call(request);
	}

	// endregion

}
