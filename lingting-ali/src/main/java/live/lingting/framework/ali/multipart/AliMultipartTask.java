package live.lingting.framework.ali.multipart;

import live.lingting.framework.ali.AliOssObject;
import live.lingting.framework.multipart.Multipart;
import live.lingting.framework.multipart.Part;
import live.lingting.framework.multipart.file.FileMultipartTask;
import live.lingting.framework.thread.Async;
import lombok.Getter;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lingting 2024-09-13 20:37
 */
public class AliMultipartTask extends FileMultipartTask<AliMultipartTask> {

	protected final AliOssObject obsObject;

	protected final Map<Part, String> map;

	@Getter
	protected final String uploadId;

	public AliMultipartTask(Multipart multipart, AliOssObject obsObject) {
		this(multipart, new Async(), obsObject);
	}

	public AliMultipartTask(Multipart multipart, Async async, AliOssObject obsObject) {
		super(multipart, async);
		this.obsObject = obsObject;
		this.map = new ConcurrentHashMap<>(multipart.getParts().size());
		this.uploadId = multipart.getId();
		setMaxRetryCount(10);
	}

	@Override
	protected void onMerge() {
		log.debug("[{}] onMerge", uploadId);

		try {
			obsObject.multipartMerge(uploadId, map);
		}
		catch (Exception e) {
			log.warn("[{}] onMerge exception", uploadId, e);
			onCancel();
		}
	}

	@Override
	protected void onCancel() {
		log.debug("[{}] onCancel", uploadId);
		obsObject.multipartCancel(uploadId);
	}

	@Override
	protected void onPart(Part part) throws Throwable {
		log.debug("[{}] onPart {}", uploadId, part.getIndex());
		try (InputStream in = multipart.stream(part)) {
			String etag = obsObject.multipartUpload(uploadId, part, in);
			map.put(part, etag);
		}
	}

}
