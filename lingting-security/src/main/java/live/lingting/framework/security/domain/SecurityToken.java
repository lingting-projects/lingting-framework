package live.lingting.framework.security.domain;

import live.lingting.framework.util.StringUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author lingting 2023-04-28 12:38
 */
@Getter
@SuppressWarnings("java:S6548")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityToken {

	public static final SecurityToken EMPTY = of(null, null, null);

	private final String type;

	private final String token;

	private final String raw;

	/**
	 * token是否有效
	 */
	public boolean isAvailable() {
		return StringUtils.hasText(getToken());
	}

	public static SecurityToken ofDelimiter(String raw, String delimiter) {
		if (!StringUtils.hasText(raw)) {
			return EMPTY;
		}

		String[] split = raw.split(delimiter, 2);
		if (split.length > 1) {
			return of(split[0], split[1], raw);
		}
		return of(null, split[0], raw);
	}

	public static SecurityToken of(String type, String token, String raw) {
		return new SecurityToken(type, token, raw);
	}

}
