package live.lingting.framework.security.properties;

/**
 * @author lingting 2023-03-29 20:50
 */
public class SecurityProperties {

	private Authorization authorization;

	/**
	 * 鉴权优先级. 降序排序
	 */
	private int order = -500;

	public Authorization getAuthorization() {return this.authorization;}

	public int getOrder() {return this.order;}

	public void setAuthorization(Authorization authorization) {this.authorization = authorization;}

	public void setOrder(int order) {this.order = order;}

	public static class Authorization {

		private boolean remote = false;

		private String remoteHost;

		public boolean isRemote() {return this.remote;}

		public String getRemoteHost() {return this.remoteHost;}

		public void setRemote(boolean remote) {this.remote = remote;}

		public void setRemoteHost(String remoteHost) {this.remoteHost = remoteHost;}
	}

}
