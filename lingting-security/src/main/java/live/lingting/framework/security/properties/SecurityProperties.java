package live.lingting.framework.security.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lingting 2023-03-29 20:50
 */
@Getter
@Setter
public class SecurityProperties {

	private Authorization authorization;

	/**
	 * 鉴权优先级. 降序排序
	 */
	private int order = -500;

	@Getter
	@Setter
	public static class Authorization {

		private boolean remote = false;

		private String remoteHost;

	}

}
