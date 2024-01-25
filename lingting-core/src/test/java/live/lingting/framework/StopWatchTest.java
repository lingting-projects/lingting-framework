package live.lingting.framework;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2024-01-25 10:39
 */
class StopWatchTest {

    @Test
    void test() {
        StopWatch watch = new StopWatch();
        assertFalse(watch.isRunning());
        watch.start();
        long nanos1 = watch.timeNanos();
        assertTrue(nanos1 > 0);
        watch.stop();
        long nanos2 = watch.timeNanos();
        assertNotEquals(nanos1, nanos2);
        long nanos3 = watch.timeNanos();
        assertEquals(nanos2, nanos3);
    }

}