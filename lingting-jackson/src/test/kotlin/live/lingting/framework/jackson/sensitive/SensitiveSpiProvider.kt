package live.lingting.framework.jackson.sensitive

import live.lingting.framework.sensitive.Sensitive
import live.lingting.framework.sensitive.Sensitive.value
import live.lingting.framework.sensitive.SensitiveProvider
import live.lingting.framework.sensitive.SensitiveSerializer
import java.io.IOException

/**
 * @author lingting 2024-01-29 10:39
 */
class SensitiveSpiProvider : SensitiveProvider {
    override fun find(sensitive: Sensitive): SensitiveSerializer {
        if (SensitiveSpiSerializer::class.java.isAssignableFrom(sensitive.value)) {
            return SensitiveSpiSerializer()
        }
        return null
    }

    class SensitiveSpiSerializer : SensitiveSerializer {
        @Throws(IOException::class)
        override fun serialize(sensitive: Sensitive, raw: String): String {
            return "*"
        }
    }
}
