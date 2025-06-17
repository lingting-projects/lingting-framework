package live.lingting.framework.reflect

import java.io.Serializable
import java.util.function.Function
import live.lingting.framework.reflect.LambdaJavaTest.JavaLambdaE
import live.lingting.framework.reflect.LambdaJavaTest.JavaLambdaV
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024/11/27 21:32
 */
class LambdaKotlinTest {

    @Test
    fun testJava() {
        val v = JavaLambdaV(
            JavaLambdaE::id,
            JavaLambdaE::name,
        )
        val m0 = LambdaMeta.of(v.l0)
        val m1 = LambdaMeta.of(v.l1)
        assertEquals(m0.cls, JavaLambdaE::class.java)
        assertEquals(m1.cls, JavaLambdaE::class.java)
        assertEquals("id", m0.field)
        assertEquals("name", m1.field)
    }

    @Test
    fun testKotlin() {
        val v = KotlinLambdaV(
            KotlinLambdaE::id,
            KotlinLambdaE::name,
        )
        val m0 = LambdaMeta.of(v.l0)
        val m1 = LambdaMeta.of(v.l1)
        assertEquals(m0.cls, KotlinLambdaE::class.java)
        assertEquals(m1.cls, KotlinLambdaE::class.java)
        assertEquals("id", m0.field)
        assertEquals("name", m1.field)
    }

}

fun interface KotlinLambdaI<T, R> : Function<T, R>, Serializable

class KotlinLambdaE {
    var id: String = ""
    var name: String = ""
}

data class KotlinLambdaV(
    val l0: KotlinLambdaI<KotlinLambdaE, *>,
    val l1: KotlinLambdaI<KotlinLambdaE, *>,
)
