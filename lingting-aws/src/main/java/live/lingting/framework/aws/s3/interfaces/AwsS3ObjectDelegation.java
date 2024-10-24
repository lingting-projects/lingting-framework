package live.lingting.framework.aws.s3.interfaces;

import live.lingting.framework.aws.AwsS3Object;
import live.lingting.framework.aws.policy.Acl;
import live.lingting.framework.aws.s3.AwsS3MultipartTask;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.multipart.Part;
import live.lingting.framework.stream.FileCloneInputStream;
import live.lingting.framework.thread.Async;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author lingting 2024-09-19 21:59
 */
public interface AwsS3ObjectDelegation extends AwsS3ObjectInterface, AwsS3Delegation<AwsS3Object> {

	@Override
	default String getKey() {
		return delegation().getKey();
	}

	@Override
	default String publicUrl() {
		return delegation().publicUrl();
	}

	@Override
	default HttpHeaders head() {
		return delegation().head();
	}

	@Override
	default void put(File file) throws IOException {
		delegation().put(file);
	}

	@Override
	default void put(File file, Acl acl) throws IOException {
		delegation().put(file, acl);
	}

	@Override
	default void put(InputStream in) throws IOException {
		delegation().put(in);
	}

	@Override
	default void put(InputStream in, Acl acl) throws IOException {
		delegation().put(in, acl);
	}

	@Override
	default void put(FileCloneInputStream in) {
		delegation().put(in);
	}

	@Override
	default void put(FileCloneInputStream in, Acl acl) {
		delegation().put(in, acl);
	}

	@Override
	default void delete() {
		delegation().delete();
	}

	@Override
	default String multipartInit() {
		return delegation().multipartInit();
	}

	@Override
	default String multipartInit(Acl acl) {
		return delegation().multipartInit(acl);
	}

	@Override
	default AwsS3MultipartTask multipart(InputStream source) throws IOException {
		return delegation().multipart(source);
	}

	@Override
	default AwsS3MultipartTask multipart(InputStream source, long parSize, Async async) throws IOException {
		return delegation().multipart(source, parSize, async);
	}

	@Override
	default String multipartUpload(String uploadId, Part part, InputStream in) {
		return delegation().multipartUpload(uploadId, part, in);
	}

	@Override
	default void multipartMerge(String uploadId, Map<Part, String> map) {
		delegation().multipartMerge(uploadId, map);
	}

	@Override
	default void multipartCancel(String uploadId) {
		delegation().multipartCancel(uploadId);
	}

}
