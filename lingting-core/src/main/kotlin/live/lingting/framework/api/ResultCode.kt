package live.lingting.framework.api

import java.util.Locale
import live.lingting.framework.exception.BizException
import live.lingting.framework.i18n.I18n

/**
 * @author lingting 2022/9/19 13:55
 */
interface ResultCode {
    /**
     * 返回的code
     * @return int
     */
    val code: Int

    /**
     * 返回消息
     * @return string
     */
    val message: String

    /**
     * 更新消息
     */
    fun with(message: String): ResultCode {
        val code = code
        return object : ResultCode {
            override val code: Int
                get() = code

            override val message: String
                get() = message
        }
    }

    /**
     * 组装消息
     * @return 返回一个新的组装消息后的对象
     */
    fun format(vararg args: Any?): ResultCode {
        val text = i18nMessage(*args)
        return with(text)
    }

    /**
     * 转异常
     */
    fun toException(vararg args: Any?): Exception {
        return toException(I18n.get(), *args)
    }

    fun toException(e: Exception?, vararg args: Any?): Exception {
        return toException(e, I18n.get(), *args)
    }

    fun toException(locale: Locale, vararg args: Any?): Exception {
        return toException(null, locale, *args)
    }

    fun toException(e: Exception?, locale: Locale, vararg args: Any?): Exception {
        val text = i18nMessage(locale, *args)
        return toException(text, e)
    }

    fun toException(): Exception {
        return toException(null)
    }

    fun toException(text: String): Exception {
        return toException(text, null)
    }

    fun toException(e: Exception?): Exception {
        return toException(null as String?, e)
    }

    fun toException(text: String?, e: Exception?): Exception {
        return BizException(this, text, e)
    }

    /**
     * 抛出异常
     */
    fun throwException() {
        throwException(null)
    }

    fun throwException(e: Exception?) {
        throw toException(e)
    }

    val i18nKey: String
        get() = "${this::class.java.name}.$code"

    fun i18nMessage(): String = i18nMessage(null)

    fun i18nMessage(vararg args: Any?) = i18nMessage(I18n.get(), *args)

    fun i18nMessage(locale: Locale, vararg args: Any?): String {
        val text = i18nMessage(locale)
        return String.format(locale, text, *args)
    }

    fun i18nMessage(locale: Locale): String {
        return I18n.find(i18nKey, message, locale)
    }

}
