package live.lingting.framework.security.grpc;

import live.lingting.framework.security.domain.AuthorizationVO;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingting 2024-01-30 20:20
 */
@Getter
@Setter
public class ExpandAuthorizationVO extends AuthorizationVO {

	private boolean isExpand;

}
