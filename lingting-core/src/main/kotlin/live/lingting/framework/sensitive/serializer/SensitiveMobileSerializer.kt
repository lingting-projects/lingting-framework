package live.lingting.framework.sensitive.serializer

import live.lingting.framework.sensitive.Sensitive
import live.lingting.framework.sensitive.SensitiveSerializer
import live.lingting.framework.sensitive.SensitiveUtils

/**
 * 手机号格式脱敏
 * +8617612349876
 * +86*****76
 * @author lingting 2024-05-21 10:20
 */
object SensitiveMobileSerializer : SensitiveSerializer {

    override fun serialize(sensitive: Sensitive, raw: String): String {
        if (raw.startsWith("+")) {
            val serialize: String = SensitiveUtils.serialize(raw.substring(1), 2, 2, sensitive)
            return "+$serialize"
        }
        return SensitiveUtils.serialize(raw, 2, 2, sensitive)
    }

}
