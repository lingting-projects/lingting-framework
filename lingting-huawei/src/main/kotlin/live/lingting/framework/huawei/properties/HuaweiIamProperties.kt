package live.lingting.framework.huawei.properties

import live.lingting.framework.time.DatePattern
import java.time.ZoneOffset

/**
 * @author lingting 2024-09-12 21:31
 */
class HuaweiIamProperties {
    var host: String = "iam.myhuaweicloud.com"

    var domain: Map<String, Any>? = null

    @JvmField
    var username: String? = null

    @JvmField
    var password: String? = null

    var zone: ZoneOffset = DatePattern.DEFAULT_ZONE_OFFSET
}
