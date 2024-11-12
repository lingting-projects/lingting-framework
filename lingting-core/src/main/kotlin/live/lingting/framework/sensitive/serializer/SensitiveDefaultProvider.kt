package live.lingting.framework.sensitive.serializer

import live.lingting.framework.sensitive.Sensitive
import live.lingting.framework.sensitive.SensitiveProvider
import live.lingting.framework.sensitive.SensitiveSerializer

/**
 * @author lingting 2024-05-21 10:30
 */
class SensitiveDefaultProvider private constructor() : SensitiveProvider {
    override fun find(sensitive: Sensitive): SensitiveSerializer {
        if (SensitiveAllSerializer::class.java.isAssignableFrom(sensitive.value)) {
            return SensitiveAllSerializer.INSTANCE
        }

        if (SensitiveMobileSerializer::class.java.isAssignableFrom(sensitive.value)) {
            return SensitiveMobileSerializer.INSTANCE
        }
        return SensitiveDefaultSerializer.INSTANCE
    }

    override val sequence: Int
        get() = Int.MAX_VALUE

    companion object {
        val INSTANCE: SensitiveDefaultProvider = SensitiveDefaultProvider()
    }
}
