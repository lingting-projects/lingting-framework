package live.lingting.framework.jackson.module

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import live.lingting.framework.jackson.serializer.InstantSerializer
import live.lingting.framework.time.DatePattern

/**
 * 自定义java8新增时间类型的序列化
 * @author Hccake
 */
class JavaTimeModule : SimpleModule() {
    init {
        addSerializer(
            LocalDateTime::class.java,
            LocalDateTimeSerializer(DatePattern.FORMATTER_YMD_HMS)
        )
        addSerializer(
            LocalDate::class.java,
            LocalDateSerializer(DatePattern.FORMATTER_YMD)
        )
        addSerializer(
            LocalTime::class.java,
            LocalTimeSerializer(DatePattern.FORMATTER_HMS)
        )
        addSerializer(Instant::class.java, InstantSerializer())
        addSerializer(OffsetDateTime::class.java, OffsetDateTimeSerializer.INSTANCE)
        addSerializer(ZonedDateTime::class.java, ZonedDateTimeSerializer.INSTANCE)

        addDeserializer(
            LocalDateTime::class.java,
            LocalDateTimeDeserializer(DatePattern.FORMATTER_YMD_HMS)
        )
        addDeserializer(
            LocalDate::class.java,
            LocalDateDeserializer(DatePattern.FORMATTER_YMD)
        )
        addDeserializer(
            LocalTime::class.java,
            LocalTimeDeserializer(DatePattern.FORMATTER_HMS)
        )
        addDeserializer(Instant::class.java, InstantDeserializer.INSTANT)
        addDeserializer(OffsetDateTime::class.java, InstantDeserializer.OFFSET_DATE_TIME)
        addDeserializer(ZonedDateTime::class.java, InstantDeserializer.ZONED_DATE_TIME)
    }
}
