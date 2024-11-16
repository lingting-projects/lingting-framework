package live.lingting.framework

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

/**
 * @author lingting 2024-01-30 11:38
 */
internal class SequenceTest {
    @Test
    fun test() {
        val list: MutableList<Any> = ArrayList()
        val e__1 = ES(-1)
        val e_2 = ES(2)
        list.add(e_2)
        list.add(ES(1))
        list.add(ES(1))
        list.add(e__1)

        Sequence.asc(list)
        assertEquals(e__1, list[0])
        assertEquals(e_2, list[list.size - 1])
        Sequence.desc(list)
        assertEquals(e_2, list[0])

        // 没有按0算. 居中
        val e_s = "es"
        list.add(e_s)
        Sequence.asc(list)
        assertEquals(e_s, list[1])

        val e_a = EA()
        list.add(e_a)
        Sequence.asc(list)
        assertEquals(e_a, list[0])

        val e_o = EO()
        list.add(e_o)
        Sequence.asc(list)
        assertEquals(e_o, list[0])
        Sequence.desc(list)
        assertEquals(e_2, list[0])
    }

    internal class ES(override val sequence: Int) : Sequence

    @Order(-90)
    internal class EA

    internal class EO : Ordered {
        override fun getOrder(): Int {
            return -100
        }
    }
}
