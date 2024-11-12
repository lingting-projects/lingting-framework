package live.lingting.framework.time

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-01-25 10:39
 */
internal class StopWatchTest {
    @Test
    fun test() {
        val watch = StopWatch()
        Assertions.assertFalse(watch.isRunning)
        watch.start()
        val nanos1 = watch.timeNanos()
        Assertions.assertTrue(nanos1 > 0)
        watch.stop()
        val nanos2 = watch.timeNanos()
        Assertions.assertNotEquals(nanos1, nanos2)
        val nanos3 = watch.timeNanos()
        Assertions.assertEquals(nanos2, nanos3)
    }
}
