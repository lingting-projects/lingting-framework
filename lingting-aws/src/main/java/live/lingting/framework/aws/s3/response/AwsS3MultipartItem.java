package live.lingting.framework.aws.s3.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author lingting 2024-09-19 20:45
 */
@Getter
@ToString
@EqualsAndHashCode
public final class AwsS3MultipartItem {

	private final String key;

	private final String uploadId;

	/**
	*/
	public AwsS3MultipartItem(String key, String uploadId) {
		this.key = key;
		this.uploadId = uploadId;
	}

	public String key() {
		return key;
	}

	public String uploadId() {
		return uploadId;
	}

}
