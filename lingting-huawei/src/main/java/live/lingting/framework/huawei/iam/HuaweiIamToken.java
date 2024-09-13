package live.lingting.framework.huawei.iam;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author lingting 2024-09-12 22:04
 */
@Getter
@Setter
public class HuaweiIamToken {

	private String value;

	private LocalDateTime expire;

	private LocalDateTime issued;

	public HuaweiIamToken() {
	}

	public HuaweiIamToken(String value, LocalDateTime expire, LocalDateTime issued) {
		this.value = value;
		this.expire = expire;
		this.issued = issued;
	}

	public Duration duration(LocalDateTime now) {
		return Duration.between(now, expire);
	}

	public boolean isExpired(Duration tokenEarlyExpire) {
		LocalDateTime now = LocalDateTime.now();
		return isExpired(tokenEarlyExpire, now);
	}

	public boolean isExpired(Duration tokenEarlyExpire, LocalDateTime now) {
		Duration duration = duration(now);
		return duration.compareTo(tokenEarlyExpire) < 1;
	}

}
