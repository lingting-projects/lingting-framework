package live.lingting.framework.security.grpc;

import live.lingting.framework.security.domain.SecurityScope;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingting 2024-01-30 20:20
 */
@Getter
@Setter
public class ExpandSecurityScope extends SecurityScope {

	private boolean isExpand;

}
