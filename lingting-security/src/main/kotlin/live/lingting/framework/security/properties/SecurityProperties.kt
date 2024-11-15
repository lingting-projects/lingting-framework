package live.lingting.framework.security.properties

/**
 * @author lingting 2023-03-29 20:50
 */
class SecurityProperties {
    var authorization: Authorization? = null

    /**
     * 鉴权优先级. 降序排序
     */
    var order: Int = -500

    class Authorization {
        var isRemote: Boolean = false

        var remoteHost: String? = null
    }
}