package live.lingting.framework.jackson.module

import live.lingting.framework.time.DatePattern
import java.time.format.DateTimeFormatter

/**
 * 精确到毫秒的序列化
 */
class JavaTimeMillisModule : JavaTimeModule() {

    override val localDateTime: DateTimeFormatter = DatePattern.FORMATTER_YMD_HMS_MILLIS

    override val localTime: DateTimeFormatter = DatePattern.FORMATTER_HMS_MILLIS

}
