package live.lingting.framework.sensitive.serializer

import live.lingting.framework.sensitive.Sensitive
import live.lingting.framework.sensitive.SensitiveProvider
import live.lingting.framework.sensitive.SensitiveSerializer

/**
 * @author lingting 2024-05-21 10:30
 */
object SensitiveDefaultProvider : SensitiveProvider {
    override fun find(sensitive: Sensitive): SensitiveSerializer {
        if (SensitiveAllSerializer::class.java.isAssignableFrom(sensitive.value.java)) {
            return SensitiveAllSerializer
        }

        if (SensitiveMobileSerializer::class.java.isAssignableFrom(sensitive.value.java)) {
            return SensitiveMobileSerializer
        }
        return SensitiveDefaultSerializer
    }

    override val sequence: Int
        get() = Int.MAX_VALUE

}
