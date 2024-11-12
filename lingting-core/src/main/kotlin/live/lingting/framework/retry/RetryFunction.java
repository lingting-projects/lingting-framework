package live.lingting.framework.retry;

import java.time.Duration;

/**
 * @author lingting 2023-12-19 13:48
 */
public interface RetryFunction {

	/**
	 * 是否允许重试
	 * @param retryCount 已重试次数
	 * @param e 当前触发异常
	 * @return true 继续重试
	 */
	boolean allowRetry(int retryCount, Exception e);

	/**
	 * 获取重试延迟
	 * @param retryCount 已重试次数
	 * @param e 当前触发异常
	 * @return 重试延迟时间
	 */
	Duration getDelay(int retryCount, Exception e);

}
