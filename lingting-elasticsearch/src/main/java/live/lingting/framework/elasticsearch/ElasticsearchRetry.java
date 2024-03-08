package live.lingting.framework.elasticsearch;

import live.lingting.framework.function.ThrowingSupplier;
import live.lingting.framework.retry.Retry;
import live.lingting.framework.retry.RetryFunction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

import static live.lingting.framework.elasticsearch.ElasticsearchUtils.isVersionConflictException;

/**
 * @author lingting 2023-12-19 14:19
 */
public class ElasticsearchRetry<T> extends Retry<T> {

	public ElasticsearchRetry(ElasticsearchProperties.Retry retry, ThrowingSupplier<T> supplier) {
		super(supplier, new ElasticsearchRetryFunction(retry));
	}

	@Getter
	@RequiredArgsConstructor
	public static class ElasticsearchRetryFunction implements RetryFunction {

		protected final ElasticsearchProperties.Retry retry;

		protected int versionConflictCount = 0;

		protected int count = 0;

		@Override
		public boolean allowRetry(int retryCount, Exception e) {
			if (!retry.isEnabled()) {
				return false;
			}

			// 版本控制异常
			if (isVersionConflictException(e)) {
				return allowVersionConflictRetry();
			}

			// 已重试次数大于等于设置重试次数
			if (retryCount >= retry.getMaxRetry()) {
				return false;
			}

			// 计数
			count++;
			return true;
		}

		protected boolean allowVersionConflictRetry() {
			// 非无限重试时,已重试次数大于等于设置重试次数
			if (retry.getVersionConflictMaxRetry() > 0 && versionConflictCount >= retry.getVersionConflictMaxRetry()) {
				return false;
			}

			// 允许重试, 计数
			versionConflictCount++;
			return true;
		}

		@Override
		public Duration getDelay(int retryCount, Exception e) {
			if (isVersionConflictException(e) && retry.getVersionConflictDelay() != null) {
				return retry.getVersionConflictDelay();
			}
			return retry.getDelay();
		}

	}

}
