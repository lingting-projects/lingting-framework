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
import java.time.format.DateTimeFormatter
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
            LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN))
        )
        addSerializer(
            LocalDate::class.java,
            LocalDateSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN))
        )
        addSerializer(
            LocalTime::class.java,
            LocalTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN))
        )
        addSerializer(Instant::class.java, InstantSerializer())
        addSerializer(OffsetDateTime::class.java, OffsetDateTimeSerializer.INSTANCE)
        addSerializer(ZonedDateTime::class.java, ZonedDateTimeSerializer.INSTANCE)

        addDeserializer(
            LocalDateTime::class.java,
            LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN))
        )
        addDeserializer(
            LocalDate::class.java,
            LocalDateDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN))
        )
        addDeserializer(
            LocalTime::class.java,
            LocalTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN))
        )
        addDeserializer(Instant::class.java, InstantDeserializer.INSTANT)
        addDeserializer(OffsetDateTime::class.java, InstantDeserializer.OFFSET_DATE_TIME)
        addDeserializer(ZonedDateTime::class.java, InstantDeserializer.ZONED_DATE_TIME)
    }
}
