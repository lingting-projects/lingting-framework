package live.lingting.framework.multipart;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author lingting 2024-09-05 14:48
 */
@Getter
@RequiredArgsConstructor
public class PartTask {

	final Part part;

	Throwable t;

	PartTaskStatus status = PartTaskStatus.WAIT;

	long retryCount = 0L;

	public boolean isCompleted() {
		return isSuccessful() || isFailed();
	}

	public boolean isSuccessful() {
		return status == PartTaskStatus.SUCCESSFUL;
	}

	public boolean isFailed() {
		return status == PartTaskStatus.FAILED;
	}

}
