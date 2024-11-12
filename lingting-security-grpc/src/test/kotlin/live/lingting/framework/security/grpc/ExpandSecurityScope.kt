package live.lingting.framework.security.grpc;

import live.lingting.framework.security.domain.SecurityScope;

/**
 * @author lingting 2024-01-30 20:20
 */
public class ExpandSecurityScope extends SecurityScope {

	private boolean isExpand;

	public boolean isExpand() {return this.isExpand;}

	public void setExpand(boolean isExpand) {this.isExpand = isExpand;}
}
