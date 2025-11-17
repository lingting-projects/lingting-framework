package live.lingting.framework.jackson.sensitive

import live.lingting.framework.sensitive.Sensitive
import live.lingting.framework.sensitive.SensitiveProvider
import live.lingting.framework.sensitive.SensitiveSerializer
import live.lingting.framework.util.ClassUtils

/**
 * @author lingting 2024-01-29 10:39
 */
class SensitiveSpiProvider : SensitiveProvider {

    override fun find(sensitive: Sensitive): SensitiveSerializer? {
        if (ClassUtils.isSuper(sensitive.value.java, SensitiveSpiSerializer::class.java)) {
            return SensitiveSpiSerializer()
        }
        return null
    }

    class SensitiveSpiSerializer : SensitiveSerializer {

        override fun serialize(sensitive: Sensitive, raw: String): String {
            return "*spi*"
        }
    }
}
