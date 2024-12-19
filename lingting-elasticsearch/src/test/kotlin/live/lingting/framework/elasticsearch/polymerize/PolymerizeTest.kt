package live.lingting.framework.elasticsearch.polymerize

import java.time.LocalDateTime
import live.lingting.framework.elasticsearch.IndexInfo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024/12/18 19:21
 */
class PolymerizeTest {

    @Test
    fun test() {
        val factory = PolymerizeFactory()
        val non = factory.get(NonPolymerize::class.java)
        val month = factory.get(MonthPolymerize::class.java) as DateTimePolymerize
        val day = factory.get(DayPolymerize::class.java) as DateTimePolymerize
        assertNotEquals(month, day)
        val info = IndexInfo(
            "index",
            "matchIndex",
            IndexInfo::class.java,
            ":_:rar#@!~.",
            non,
            emptyList(),
            0
        )

        assertEquals(info.index, non.index(info))
        assertEquals(info.matchIndex, month.index(info))
        assertEquals(info.matchIndex, day.index(info))

        val time = LocalDateTime.of(2024, 1, 1, 0, 0, 0, 0)

        assertEquals("${info.index}${info.separate}2024${info.separate}01", month.index(info, time))
        assertEquals("${info.index}${info.separate}2024${info.separate}01${info.separate}01", day.index(info, time))
    }
}
