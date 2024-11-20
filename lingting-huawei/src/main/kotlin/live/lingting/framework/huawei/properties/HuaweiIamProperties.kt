package live.lingting.framework.huawei.properties

import java.time.ZoneOffset
import live.lingting.framework.time.DatePattern

/**
 * @author lingting 2024-09-12 21:31
 */
class HuaweiIamProperties {
    var host: String = "iam.myhuaweicloud.com"

    var domain: Map<String, Any> = emptyMap()

    var username: String = ""

    var password: String = ""

    var zone: ZoneOffset = DatePattern.DEFAULT_ZONE_OFFSET
}
