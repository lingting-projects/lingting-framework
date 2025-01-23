package live.lingting.framework.concurrent

import java.util.concurrent.TimeoutException
import kotlin.test.assertEquals
import live.lingting.framework.util.DurationUtils.millis
import live.lingting.framework.value.WaitValue
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author lingting 2025/1/22 18:54
 */
class AwaitTest {

    @Test
    fun test() {
        assertTrue(Await.waitTrue { true })
        assertFalse(Await.waitFalse { false })
        assertThrows(TimeoutException::class.java) { Await.waitFalse(100.millis) { true } }
        assertDoesNotThrow { Await.waitFalse(100.millis) { false } }

        val wait = WaitValue.of(0)
        assertThrows(TimeoutException::class.java) {
            try {
                Await.waitTrue(100.millis) {
                    Await.wait(500.millis)
                    wait.value = 2
                    true
                }
            } catch (e: TimeoutException) {
                wait.value = 1
                throw e
            }
        }
        Await.waitTrue { wait.value != 0 }
        assertEquals(1, wait.value)
        Await.wait(500.millis)
        assertEquals(1, wait.value)


        assertThrows(TimeoutException::class.java) {
            try {
                Await.waitTrue(300.millis) {
                    try {
                        Await.waitTrue(500.millis) {
                            Await.wait(600.millis)
                            wait.value = 4
                            true
                        }
                    } catch (_: TimeoutException) {
                        //
                    }

                    wait.value = 3
                    true
                }
            } catch (e: TimeoutException) {
                wait.value = 2
                throw e
            }
        }

        Await.waitTrue { wait.value != 1 }
        assertEquals(2, wait.value)
        Await.wait(600.millis)
        assertEquals(2, wait.value)

    }

}
