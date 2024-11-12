package live.lingting.framework.multipart;

/**
 * @author lingting 2024-09-05 14:48
 */
public class PartTask {

	final Part part;

	Throwable t;

	PartTaskStatus status = PartTaskStatus.WAIT;

	long retryCount = 0L;

	public PartTask(Part part) {
		this.part = part;
	}

	public boolean isCompleted() {
		return isSuccessful() || isFailed();
	}

	public boolean isSuccessful() {
		return status == PartTaskStatus.SUCCESSFUL;
	}

	public boolean isFailed() {
		return status == PartTaskStatus.FAILED;
	}

	public Part getPart() {return this.part;}

	public Throwable getT() {return this.t;}

	public PartTaskStatus getStatus() {return this.status;}

	public long getRetryCount() {return this.retryCount;}
}
