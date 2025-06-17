package live.lingting.framework.jackson.module

import com.fasterxml.jackson.core.util.JacksonFeatureSet
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import com.fasterxml.jackson.datatype.jsr310.JavaTimeFeature
import com.fasterxml.jackson.datatype.jsr310.deser.DurationDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310StringParsableDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.JavaTimeDeserializerModifier
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.MonthDayDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.OffsetTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.YearDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.key.DurationKeyDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.key.InstantKeyDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.key.LocalDateKeyDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.key.LocalDateTimeKeyDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.key.LocalTimeKeyDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.key.MonthDayKeyDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.key.OffsetDateTimeKeyDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.key.OffsetTimeKeyDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.key.PeriodKeyDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.key.YearKeyDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.key.YearMonthKeyDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.key.ZoneIdKeyDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.key.ZoneOffsetKeyDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.key.ZonedDateTimeKeyDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.JavaTimeSerializerModifier
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.MonthDaySerializer
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.YearSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.ZoneIdSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.key.ZonedDateTimeKeySerializer
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.MonthDay
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.Period
import java.time.Year
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import live.lingting.framework.time.DatePattern

/**
 * 自定义java8新增时间类型的序列化
 * <p>basic copy  from com.fasterxml.jackson.datatype.jsr310.JavaTimeModule</p>
 * @author Hccake
 */
open class JavaTimeModule @JvmOverloads constructor(
    protected val features: JacksonFeatureSet<JavaTimeFeature> = JacksonFeatureSet.fromDefaults(JavaTimeFeature.entries.toTypedArray())
) : SimpleModule() {

    open val localDateTime = DatePattern.FORMATTER_YMD_HMS
    open val localDate = DatePattern.FORMATTER_YMD
    open val localTime = DatePattern.FORMATTER_HMS

    val oneBasedMonthEnabled = features.isEnabled(JavaTimeFeature.ONE_BASED_MONTHS)

    val deserializerModifier = JavaTimeDeserializerModifier(oneBasedMonthEnabled)

    val serializerModifier = JavaTimeSerializerModifier(oneBasedMonthEnabled)

    protected var init = false
        private set

    fun init() {
        if (init) {
            return
        }
        // Instant variants:
        addDeserializer(Instant::class.java, InstantDeserializer.INSTANT.withFeatures(features))
        addDeserializer(OffsetDateTime::class.java, InstantDeserializer.OFFSET_DATE_TIME.withFeatures(features))
        addDeserializer(ZonedDateTime::class.java, InstantDeserializer.ZONED_DATE_TIME.withFeatures(features))

        // Other deserializers
        addDeserializer(Duration::class.java, DurationDeserializer.INSTANCE)
        addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(localDateTime))
        addDeserializer(LocalDate::class.java, LocalDateDeserializer(localDate))
        addDeserializer(LocalTime::class.java, LocalTimeDeserializer(localTime))
        addDeserializer(MonthDay::class.java, MonthDayDeserializer.INSTANCE)
        addDeserializer(OffsetTime::class.java, OffsetTimeDeserializer.INSTANCE)
        addDeserializer(Period::class.java, JSR310StringParsableDeserializer.PERIOD)
        addDeserializer(Year::class.java, YearDeserializer.INSTANCE)
        addDeserializer(YearMonth::class.java, YearMonthDeserializer.INSTANCE)
        addDeserializer(ZoneId::class.java, JSR310StringParsableDeserializer.ZONE_ID)
        addDeserializer(ZoneOffset::class.java, JSR310StringParsableDeserializer.ZONE_OFFSET)

        addSerializer(Duration::class.java, DurationSerializer.INSTANCE)
        addSerializer(Instant::class.java, InstantSerializer.INSTANCE)
        addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(localDateTime))
        addSerializer(LocalDate::class.java, LocalDateSerializer(localDate))
        addSerializer(LocalTime::class.java, LocalTimeSerializer(localTime))
        addSerializer(MonthDay::class.java, MonthDaySerializer.INSTANCE)
        addSerializer(OffsetDateTime::class.java, OffsetDateTimeSerializer.INSTANCE)
        addSerializer(OffsetTime::class.java, OffsetTimeSerializer.INSTANCE)
        addSerializer(Period::class.java, ToStringSerializer(Period::class.java))
        addSerializer(Year::class.java, YearSerializer.INSTANCE)
        addSerializer(YearMonth::class.java, YearMonthSerializer.INSTANCE)

        /* 27-Jun-2015, tatu: This is the real difference from the old
         *  {@link JSR310Module}: default is to produce ISO-8601 compatible
         *  serialization with timezone offset only, not timezone id.
         *  But this is configurable.
         */
        addSerializer(ZonedDateTime::class.java, ZonedDateTimeSerializer.INSTANCE)

        // since 2.11: need to override Type Id handling
        // (actual concrete type is `ZoneRegion`, but that's not visible)
        addSerializer(ZoneId::class.java, ZoneIdSerializer())
        addSerializer(ZoneOffset::class.java, ToStringSerializer(ZoneOffset::class.java))

        // key deserializers
        addKeyDeserializer(Duration::class.java, DurationKeyDeserializer.INSTANCE)
        addKeyDeserializer(Instant::class.java, InstantKeyDeserializer.INSTANCE)
        addKeyDeserializer(LocalDateTime::class.java, LocalDateTimeKeyDeserializer.INSTANCE)
        addKeyDeserializer(LocalDate::class.java, LocalDateKeyDeserializer.INSTANCE)
        addKeyDeserializer(LocalTime::class.java, LocalTimeKeyDeserializer.INSTANCE)
        addKeyDeserializer(MonthDay::class.java, MonthDayKeyDeserializer.INSTANCE)
        addKeyDeserializer(OffsetDateTime::class.java, OffsetDateTimeKeyDeserializer.INSTANCE)
        addKeyDeserializer(OffsetTime::class.java, OffsetTimeKeyDeserializer.INSTANCE)
        addKeyDeserializer(Period::class.java, PeriodKeyDeserializer.INSTANCE)
        addKeyDeserializer(Year::class.java, YearKeyDeserializer.INSTANCE)
        addKeyDeserializer(YearMonth::class.java, YearMonthKeyDeserializer.INSTANCE)
        addKeyDeserializer(ZonedDateTime::class.java, ZonedDateTimeKeyDeserializer.INSTANCE)
        addKeyDeserializer(ZoneId::class.java, ZoneIdKeyDeserializer.INSTANCE)
        addKeyDeserializer(ZoneOffset::class.java, ZoneOffsetKeyDeserializer.INSTANCE)

        // key serializers
        addKeySerializer(ZonedDateTime::class.java, ZonedDateTimeKeySerializer.INSTANCE)
        init = true
    }

    override fun setupModule(context: SetupContext) {
        init()
        super.setupModule(context)
        context.addBeanDeserializerModifier(deserializerModifier)
        context.addBeanSerializerModifier(serializerModifier)
    }

}
