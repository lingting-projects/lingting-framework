package live.lingting.framework.jackson.module

import com.fasterxml.jackson.core.util.JacksonFeatureSet
import com.fasterxml.jackson.datatype.jsr310.JavaTimeFeature
import java.time.format.DateTimeFormatter
import live.lingting.framework.time.DatePattern

/**
 * 精确到毫秒的序列化
 */
class JavaTimeMillisModule @JvmOverloads constructor(
    features: JacksonFeatureSet<JavaTimeFeature> = JacksonFeatureSet.fromDefaults(JavaTimeFeature.entries.toTypedArray())
) : JavaTimeModule(features) {

    override val localDateTime: DateTimeFormatter = DatePattern.FORMATTER_YMD_HMS_MILLIS

    override val localTime: DateTimeFormatter = DatePattern.FORMATTER_HMS_MILLIS

}
