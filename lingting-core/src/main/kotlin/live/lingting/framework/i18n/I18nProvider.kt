package live.lingting.framework.i18n

/**
 * @author lingting 2024/11/30 18:27
 */
fun interface I18nProvider {

    fun load(): Collection<I18nSource>

}
