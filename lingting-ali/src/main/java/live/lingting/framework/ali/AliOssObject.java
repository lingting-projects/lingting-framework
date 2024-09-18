package live.lingting.framework.ali;

import live.lingting.framework.ali.oss.AliOssEmptyRequest;
import live.lingting.framework.ali.oss.AliOssHeaders;
import live.lingting.framework.ali.oss.AliOssObjectPutRequest;
import live.lingting.framework.ali.oss.AliOssRequest;
import live.lingting.framework.ali.properties.AliOssProperties;
import live.lingting.framework.http.HttpMethod;
import live.lingting.framework.http.HttpResponse;
import live.lingting.framework.http.HttpUrlBuilder;
import live.lingting.framework.s3.Acl;
import live.lingting.framework.stream.CloneInputStream;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static live.lingting.framework.http.HttpMethod.DELETE;

/**
 * @author lingting 2024-09-18 10:29
 */
@Getter
public class AliOssObject extends AliOss {

	private final String key;

	protected AliOssObject(AliOssProperties properties, String key) {
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
		// todo
		return new AliOssHeaders(null);
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
		AliOssObjectPutRequest request = new AliOssObjectPutRequest();
		request.setStream(in);
		request.setAcl(acl);
		call(request);
	}

	public void delete() {
		AliOssEmptyRequest request = new AliOssEmptyRequest(DELETE);
		call(request);
	}

	// endregion

}
