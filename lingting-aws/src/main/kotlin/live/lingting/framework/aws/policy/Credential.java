package live.lingting.framework.aws.policy;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author lingting 2024-09-12 20:38
 */
public class Credential {

	protected final String ak;

	protected final String sk;

	protected final String token;

	protected final LocalDateTime expire;

	public Credential(String ak, String sk, String token, LocalDateTime expire) {
		this.ak = ak;
		this.sk = sk;
		this.token = token;
		this.expire = expire;
	}

	public Duration between() {
		LocalDateTime now = LocalDateTime.now();
		return between(now);
	}

	/**
	 * 计算指定时间到过期时间还需要花费的时间.
	 *
	 * @return 如果小于等于0 表示 已经过期
	 */
	public Duration between(LocalDateTime now) {
		return Duration.between(now, expire);
	}

	public boolean isExpired() {
		Duration duration = between();
		return duration.isZero() || duration.isNegative();
	}

	public String getAk() {return this.ak;}

	public String getSk() {return this.sk;}

	public String getToken() {return this.token;}

	public LocalDateTime getExpire() {return this.expire;}
}
