package live.lingting.framework.util

import live.lingting.framework.util.DurationUtils.days
import live.lingting.framework.util.DurationUtils.hours
import live.lingting.framework.util.DurationUtils.minutes
import live.lingting.framework.util.DurationUtils.months
import live.lingting.framework.util.DurationUtils.seconds
import live.lingting.framework.util.DurationUtils.weeks
import live.lingting.framework.util.DurationUtils.years
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author lingting 2025/12/2 14:03
 */
class DurationUtilsTest {

    @Test
    fun test() {
        assertEquals(1.5.hours, 90.minutes)
        assertEquals(1.weeks, 7.days)
        assertEquals(0.5.weeks, 3.5.days)
        assertEquals(0.5.days, 12.hours)
        assertEquals(0.5.minutes, 30.seconds)
        assertEquals(0.1.minutes, 6.seconds)
        assertEquals(0.5.months, 15.days)
        assertEquals(0.5.years, 182.5.days)
    }

}
