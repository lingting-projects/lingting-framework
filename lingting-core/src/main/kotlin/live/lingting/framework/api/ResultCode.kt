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
    val code: Long

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
            override val code: Long
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
        val message: String = String.format(message, *args)
        return with(message)
    }

    /**
     * 转异常
     */
    fun toException(): Exception {
        return toException(null)
    }

    fun toException(e: Exception?): Exception {
        return BizException(this, e)
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

    fun i18nMessage(): String = i18nMessage(I18n.get())

    fun i18nMessage(locale: Locale): String {
        return I18n.find(i18nKey, message, locale)
    }

}
