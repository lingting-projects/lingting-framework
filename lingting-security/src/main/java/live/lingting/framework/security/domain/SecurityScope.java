package live.lingting.framework.security.domain;

import live.lingting.framework.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author lingting 2023-03-29 20:25
 */
@Getter
@Setter
public class SecurityScope {

	private String token;

	private String userId;

	private String tenantId;

	private String username;

	private String password;

	private String avatar;

	private String nickname;

	private Boolean isSystem;

	/**
	 * 是否启用
	 */
	private Boolean isEnabled;

	/**
	 * 过期时间的时间戳
	 */
	private Long expireTime;

	private Set<String> roles;

	private Set<String> permissions;

	private Map<String, Object> attributes;

	public boolean isSystem() {
		return Boolean.TRUE.equals(getIsSystem());
	}

	public boolean enabled() {
		return Boolean.TRUE.equals(getIsEnabled());
	}

	/**
	 * 此scope是否为已登录用户
	 */
	public boolean isLogin() {
		boolean tokenAvailable = StringUtils.hasText(getToken());
		boolean userAvailable = StringUtils.hasText(getUserId());
		boolean enableAvailable = getIsEnabled() != null;
		return tokenAvailable && userAvailable && getIsSystem() != null && enableAvailable;
	}

	public Object attribute(String key) {return attributes == null || attributes.isEmpty() ? null : attributes.get(key);}

	public <T> T attribute(String key, Function<Optional<Object>, T> func) {
		return func.apply(Optional.ofNullable(attribute(key)));
	}

	public <T> T attribute(String key, T defaultValue, Function<Optional<Object>, T> func) {
		return attribute(key, defaultValue, Optional::isEmpty, func);
	}

	/**
	 * @param usingDefault 如果返回true表示使用默认值
	 */
	public <T> T attribute(String key, T defaultValue, Predicate<Optional<Object>> usingDefault, Function<Optional<Object>, T> func) {
		Optional<Object> optional = Optional.ofNullable(attribute(key));
		if (usingDefault.test(optional)) {
			return defaultValue;
		}
		return func.apply(optional);
	}

}
