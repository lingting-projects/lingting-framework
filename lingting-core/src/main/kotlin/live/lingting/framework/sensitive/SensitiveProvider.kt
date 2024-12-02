package live.lingting.framework.sensitive

import live.lingting.framework.Sequence

/**
 * @author lingting 2024-01-26 18:16
 */
interface SensitiveProvider : Sequence {

    fun find(sensitive: Sensitive): SensitiveSerializer?

    override val sequence: Int
        get() = 0
}
