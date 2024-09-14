package live.lingting.framework.huawei.multipart;

import live.lingting.framework.huawei.HuaweiObsObject;
import live.lingting.framework.multipart.Multipart;
import live.lingting.framework.multipart.Part;
import live.lingting.framework.multipart.file.FileMultipartTask;
import live.lingting.framework.retry.Retry;
import live.lingting.framework.thread.Async;
import lombok.Getter;

import java.io.InputStream;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lingting 2024-09-13 20:37
 */
public class HuaweiMultipartTask extends FileMultipartTask<HuaweiMultipartTask> {

	protected final HuaweiObsObject obsObject;

	protected final Map<Part, String> map;

	@Getter
	protected final String uploadId;

	public HuaweiMultipartTask(Multipart multipart, HuaweiObsObject obsObject) {
		this(multipart, new Async(), obsObject);
	}

	public HuaweiMultipartTask(Multipart multipart, Async async, HuaweiObsObject obsObject) {
		super(multipart, async);
		this.obsObject = obsObject;
		this.map = new ConcurrentHashMap<>(multipart.getParts().size());
		this.uploadId = multipart.getId();
		setMaxRetryCount(10);
	}

	@Override
	protected void onMerge() {
		log.debug("[{}] onMerge", uploadId);

		Retry<Void> retry = Retry.simple((int) maxRetryCount, Duration.ofSeconds(1), () -> {
			Exception ex = null;
			try {
				obsObject.multipartMerge(uploadId, map);
			}
			catch (Exception e) {
				ex = e;
			}

			// 尝试进行校验
			if (ex != null) {
				String headUploadId = obsObject.head().multipartUploadId();
				// 不匹配, 未合并成功. 抛出异常 稍后重试
				if (!Objects.equals(headUploadId, uploadId)) {
					log.debug("[{}] onMerge retry", uploadId);
					throw ex;
				}
			}
			return null;
		});

		try {
			retry.get();
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
