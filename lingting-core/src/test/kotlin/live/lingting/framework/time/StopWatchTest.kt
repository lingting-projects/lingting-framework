package live.lingting.framework.time

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-01-25 10:39
 */
internal class StopWatchTest {
    @Test
    fun test() {
        val watch = StopWatch()
        assertFalse(watch.isRunning)
        watch.start()
        val nanos1 = watch.timeNanos()
        assertTrue(nanos1 > 0)
        watch.stop()
        val nanos2 = watch.timeNanos()
        assertNotEquals(nanos1, nanos2)
        val nanos3 = watch.timeNanos()
        assertEquals(nanos2, nanos3)
    }
}
