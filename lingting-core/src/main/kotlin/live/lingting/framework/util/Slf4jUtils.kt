package live.lingting.framework.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author lingting 2024/11/27 13:42
 */
object Slf4jUtils {

    @JvmStatic
    fun <T : Any> T.logger(): Logger {
        return LoggerFactory.getLogger(this.javaClass)
    }

    @JvmStatic
    fun <T : Any> T.logger(cls: Class<*>): Logger {
        return LoggerFactory.getLogger(cls)
    }

    @JvmStatic
    fun <T : Any> T.logger(name: String): Logger {
        return LoggerFactory.getLogger(name)
    }

    inline val InlineLogger.log get() = logger()

    interface InlineLogger

}
