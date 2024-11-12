package live.lingting.framework.security.grpc

import live.lingting.framework.security.domain.AuthorizationVO

/**
 * @author lingting 2024-01-30 20:20
 */
class ExpandAuthorizationVO : AuthorizationVO() {
    var isExpand: Boolean = false
}
