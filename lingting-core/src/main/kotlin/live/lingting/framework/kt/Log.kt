package live.lingting.framework.kt

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

/**
 * @author lingting 2024/11/12 18:57
 */

fun <T : Any> T.logger(): Logger {
    return LoggerFactory.getLogger(this.javaClass)
}

fun <T : Any> T.logger(cls: Class<*>): Logger {
    return LoggerFactory.getLogger(cls)
}

fun <T : Any> T.logger(cls: KClass<*>): Logger {
    return LoggerFactory.getLogger(cls.java)
}

fun <T : Any> T.logger(name: String): Logger {
    return LoggerFactory.getLogger(name)
}
