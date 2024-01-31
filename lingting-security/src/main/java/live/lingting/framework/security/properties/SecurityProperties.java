package live.lingting.framework.security.properties;

import lombok.Data;

/**
 * @author lingting 2023-03-29 20:50
 */
@Data
public class SecurityProperties {

	private Authorization authorization;

	@Data
	public static class Authorization {

		private boolean remote = false;

		private String remoteHost;

	}

}
