package live.lingting.framework.aws.policy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author lingting 2024-09-12 20:38
 */
@Getter
@RequiredArgsConstructor
public class Credential {

	protected final String ak;

	protected final String sk;

	protected final String token;

	protected final LocalDateTime expire;

	public Duration between() {
		LocalDateTime now = LocalDateTime.now();
		return between(now);
	}

	/**
	 * 计算指定时间到过期时间还需要花费的时间.
	 * @return 如果小于等于0 表示 已经过期
	 */
	public Duration between(LocalDateTime now) {
		return Duration.between(now, expire);
	}

	public boolean isExpired() {
		Duration duration = between();
		return duration.isZero() || duration.isNegative();
	}

}
