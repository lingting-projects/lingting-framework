package live.lingting.framework.aws.s3;

import live.lingting.framework.aws.AwsS3Object;
import live.lingting.framework.multipart.Multipart;
import live.lingting.framework.multipart.Part;
import live.lingting.framework.multipart.file.FileMultipartTask;
import live.lingting.framework.thread.Async;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lingting 2024-09-19 20:26
 */
public class AwsS3MultipartTask extends FileMultipartTask<AwsS3MultipartTask> {

	protected final AwsS3Object s3;

	protected final Map<Part, String> map;

	protected final String uploadId;

	public AwsS3MultipartTask(Multipart multipart, AwsS3Object s3) {
		this(multipart, new Async(), s3);
	}

	public AwsS3MultipartTask(Multipart multipart, Async async, AwsS3Object s3) {
		super(multipart, async);
		this.s3 = s3;
		this.map = new ConcurrentHashMap<>(multipart.getParts().size());
		this.uploadId = multipart.getId();
		setMaxRetryCount(10);
	}

	@Override
	protected void onMerge() {
		try {
			s3.multipartMerge(uploadId, map);
		}
		catch (Exception e) {
			log.warn("[{}] onMerge exception", uploadId, e);
			onCancel();
			log.debug("[{}] onCanceled", uploadId);
		}
	}

	@Override
	protected void onCancel() {
		s3.multipartCancel(uploadId);
	}

	@Override
	protected void onPart(Part part) throws Throwable {
		try (InputStream in = multipart.stream(part)) {
			String etag = s3.multipartUpload(uploadId, part, in);
			map.put(part, etag);
		}
	}

	public String getUploadId() {return this.uploadId;}
}
