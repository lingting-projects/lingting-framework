package live.lingting.framework.util

import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

/**
 * @author lingting 2025/6/17 15:20
 */
object TimeUnitUtils {

    @JvmStatic
    fun TimeUnit.toChronoUnit(): ChronoUnit {
        return when (this) {
            TimeUnit.NANOSECONDS -> ChronoUnit.NANOS
            TimeUnit.MICROSECONDS -> ChronoUnit.MICROS
            TimeUnit.MILLISECONDS -> ChronoUnit.MILLIS
            TimeUnit.SECONDS -> ChronoUnit.SECONDS
            TimeUnit.MINUTES -> ChronoUnit.MINUTES
            TimeUnit.HOURS -> ChronoUnit.HOURS
            TimeUnit.DAYS -> ChronoUnit.DAYS
        }
    }

}
