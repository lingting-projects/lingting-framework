package live.lingting.framework.util

import java.time.LocalDateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


/**
 * @author lingting 2024/12/30 15:31
 */
class LocalDateTimeUtilsTest {

    @Test
    fun test() {
        val start = LocalDateTime.of(2024, 12, 30, 0, 0, 4)
        val end = start.plusDays(3)
        assertEquals(2024, start.year)
        assertEquals(2025, end.year)
    }

}
