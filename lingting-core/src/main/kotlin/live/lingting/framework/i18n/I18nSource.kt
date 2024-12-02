package live.lingting.framework.i18n

import java.util.Locale
import live.lingting.framework.Sequence

/**
 * @author lingting 2024/11/30 18:28
 */
interface I18nSource : Sequence {

    val locale: Locale

    /**
     * 降序排序
     */
    override val sequence: Int get() = 0

    fun find(key: String): String?

}
