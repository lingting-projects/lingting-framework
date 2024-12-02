package live.lingting.framework.i18n

import live.lingting.framework.Sequence

/**
 * @author lingting 2024/11/30 18:27
 */
interface I18nProvider : Sequence {

    fun load(): Collection<I18nSource>

    /**
     * 降序排序
     */
    override val sequence: Int
        get() = 0

}
