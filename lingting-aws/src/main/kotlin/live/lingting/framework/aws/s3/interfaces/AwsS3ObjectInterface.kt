package live.lingting.framework.aws.s3.interfaces;

import live.lingting.framework.aws.policy.Acl;
import live.lingting.framework.aws.s3.AwsS3MultipartTask;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.multipart.Part;
import live.lingting.framework.stream.CloneInputStream;
import live.lingting.framework.thread.Async;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author lingting 2024-09-19 21:59
 */
public interface AwsS3ObjectInterface {

	// region get

	String getKey();

	String publicUrl();

	HttpHeaders head();

	// endregion
	// region put
	void put(File file) throws IOException;

	void put(File file, Acl acl) throws IOException;

	void put(InputStream in) throws IOException;

	void put(InputStream in, Acl acl) throws IOException;

	void put(CloneInputStream in);

	void put(CloneInputStream in, Acl acl);

	void delete();

	// endregion
	// region multipart
	String multipartInit();

	String multipartInit(Acl acl);

	AwsS3MultipartTask multipart(InputStream source) throws IOException;

	AwsS3MultipartTask multipart(InputStream source, long parSize, Async async) throws IOException;

	String multipartUpload(String uploadId, Part part, InputStream in);

	void multipartMerge(String uploadId, Map<Part, String> map);

	void multipartCancel(String uploadId);
	// endregion

}
