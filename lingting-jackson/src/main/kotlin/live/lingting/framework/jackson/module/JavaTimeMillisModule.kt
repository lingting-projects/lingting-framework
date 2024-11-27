package live.lingting.framework.jackson.module

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import java.time.LocalDateTime
import java.time.LocalTime
import live.lingting.framework.time.DatePattern

/**
 * 精确到毫秒的序列化
 */
class JavaTimeMillisModule : SimpleModule() {
    init {
        addSerializer(
            LocalDateTime::class.java,
            LocalDateTimeSerializer(DatePattern.FORMATTER_YMD_HMS_MILLIS)
        )
        addSerializer(
            LocalTime::class.java,
            LocalTimeSerializer(DatePattern.FORMATTER_HMS_MILLIS)
        )

        addDeserializer(
            LocalDateTime::class.java,
            LocalDateTimeDeserializer(DatePattern.FORMATTER_YMD_HMS_MILLIS)
        )
        addDeserializer(
            LocalTime::class.java,
            LocalTimeDeserializer(DatePattern.FORMATTER_HMS_MILLIS)
        )
    }
}
