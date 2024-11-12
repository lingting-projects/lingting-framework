package live.lingting.framework.elasticsearch;

import java.time.Duration;

/**
 * @author lingting 2024-03-06 16:43
 */
public class ElasticsearchProperties {

	/**
	 * 重试配置
	 */
	protected Retry retry = new Retry();

	/**
	 * 滚动查询配置
	 */
	protected Scroll scroll = new Scroll();

	public Retry getRetry() {return this.retry;}

	public Scroll getScroll() {return this.scroll;}

	public void setRetry(Retry retry) {this.retry = retry;}

	public void setScroll(Scroll scroll) {this.scroll = scroll;}

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

		public boolean isEnabled() {return this.enabled;}

		public int getMaxRetry() {return this.maxRetry;}

		public Duration getDelay() {return this.delay;}

		public int getVersionConflictMaxRetry() {return this.versionConflictMaxRetry;}

		public Duration getVersionConflictDelay() {return this.versionConflictDelay;}

		public void setEnabled(boolean enabled) {this.enabled = enabled;}

		public void setMaxRetry(int maxRetry) {this.maxRetry = maxRetry;}

		public void setDelay(Duration delay) {this.delay = delay;}

		public void setVersionConflictMaxRetry(int versionConflictMaxRetry) {this.versionConflictMaxRetry = versionConflictMaxRetry;}

		public void setVersionConflictDelay(Duration versionConflictDelay) {this.versionConflictDelay = versionConflictDelay;}
	}

	public static class Scroll {

		/**
		 * 滚动索引保留超时时间
		 */
		private Duration timeout;

		/**
		 * 滚动查询时每次查询数量
		 */
		private Long size;

		public Duration getTimeout() {return this.timeout;}

		public Long getSize() {return this.size;}

		public void setTimeout(Duration timeout) {this.timeout = timeout;}

		public void setSize(Long size) {this.size = size;}
	}

}
