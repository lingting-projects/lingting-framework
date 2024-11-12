package live.lingting.framework.security.grpc

import live.lingting.framework.security.domain.SecurityScope

/**
 * @author lingting 2024-01-30 20:20
 */
class ExpandSecurityScope : SecurityScope() {
    var isExpand: Boolean = false
}
