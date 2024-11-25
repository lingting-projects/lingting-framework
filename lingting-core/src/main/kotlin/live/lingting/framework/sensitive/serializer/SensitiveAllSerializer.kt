package live.lingting.framework.sensitive.serializer

import live.lingting.framework.sensitive.Sensitive
import live.lingting.framework.sensitive.SensitiveSerializer

/**
 * 全脱敏
 * 这是一个要脱敏的文本
 * *****
 * @author lingting 2024-05-21 10:20
 */
object SensitiveAllSerializer : SensitiveSerializer {

    override fun serialize(sensitive: Sensitive, raw: String): String {
        return sensitive.middle
    }

}
