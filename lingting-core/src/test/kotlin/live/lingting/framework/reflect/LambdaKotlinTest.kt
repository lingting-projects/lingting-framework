package live.lingting.framework.reflect

import java.io.Serializable
import java.util.function.Function
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024/11/27 21:32
 */
class LambdaKotlinTest {

    @Test
    fun test() {
        val v = LambdaV(
            LambdaE::id,
            LambdaE::name,
        )
        val m0 = LambdaMeta.of(v.l0)
        val m1 = LambdaMeta.of(v.l1)
        assertEquals(m0.cls, LambdaE::class.java)
        assertEquals(m1.cls, LambdaE::class.java)
        assertEquals("id", m0.field)
        assertEquals("name", m1.field)
    }

}

fun interface LambdaI<T, R> : Function<T, R>, Serializable

class LambdaE {
    var id: String = ""
    var name: String = ""
}

data class LambdaV(
    val l0: LambdaI<LambdaE, *>,
    val l1: LambdaI<LambdaE, *>,
)
