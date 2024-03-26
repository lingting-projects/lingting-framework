package live.lingting.framework.elasticsearch;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

/**
 * @author lingting 2024-03-06 16:43
 */
@Getter
@Setter
public class ElasticsearchProperties {

	/**
	 * 重试配置
	 */
	protected Retry retry = new Retry();

	@Getter
	@Setter
	public static class Retry {

		private boolean enabled = false;

		/**
		 * 最大重试次数
		 */
		private int maxRetry = 3;

		/**
		 * 每次重试延迟
		 */
		private Duration delay = Duration.ofMillis(50);

		/**
		 * 触发版本冲突时重试次数, 小于0表示无限重试
		 * <p>
		 * 此重试独立计数, 不论是否达到 {@link Retry#getMaxRetry()} 均会按照此配置进行重试
		 * </p>
		 */
		private int versionConflictMaxRetry = 50;

		/**
		 * 版本冲突重试延迟, 未配置则按照 {@link Retry#getDelay()} 进行
		 */
		private Duration versionConflictDelay;

	}

	/**
	 * 滚动查询配置
	 */
	protected Scroll scroll = new Scroll();

	@Getter
	@Setter
	public static class Scroll {

		/**
		 * 滚动索引保留超时时间
		 */
		private Duration timeout;

		/**
		 * 滚动查询时每次查询数量
		 */
		private Long size;

	}

}
