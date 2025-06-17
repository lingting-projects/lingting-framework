package live.lingting.framework.security.properties

/**
 * @author lingting 2023-03-29 20:50
 */
class SecurityProperties {

    var authorization: Authorization = Authorization()

    /**
     * 鉴权优先级. 降序排序
     */
    var order: Int = Int.MIN_VALUE + 10000

    class Authorization {
        var remote: Boolean = false

        var remoteHost: String? = null
    }
}
