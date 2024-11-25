package live.lingting.framework.sensitive.serializer

import live.lingting.framework.sensitive.Sensitive
import live.lingting.framework.sensitive.SensitiveSerializer
import live.lingting.framework.sensitive.SensitiveUtils

/**
 * 默认脱敏
 * 这是一个要脱敏的文本
 * 这*****本
 * @author lingting 2024-05-21 10:20
 */
object SensitiveDefaultSerializer : SensitiveSerializer {

    override fun serialize(sensitive: Sensitive, raw: String): String {
        return SensitiveUtils.serialize(raw, 1, 1, sensitive)
    }

}
