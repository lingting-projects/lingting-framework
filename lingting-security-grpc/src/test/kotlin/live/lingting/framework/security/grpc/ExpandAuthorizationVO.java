package live.lingting.framework.security.grpc;

import live.lingting.framework.security.domain.AuthorizationVO;

/**
 * @author lingting 2024-01-30 20:20
 */
public class ExpandAuthorizationVO extends AuthorizationVO {

	private boolean isExpand;

	public boolean isExpand() {return this.isExpand;}

	public void setExpand(boolean isExpand) {this.isExpand = isExpand;}
}
