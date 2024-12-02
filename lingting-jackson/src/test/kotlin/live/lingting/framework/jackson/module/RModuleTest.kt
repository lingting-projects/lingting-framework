package live.lingting.framework.jackson.module

import com.fasterxml.jackson.core.type.TypeReference
import java.util.Locale
import live.lingting.framework.api.R
import live.lingting.framework.i18n.I18n
import live.lingting.framework.jackson.JacksonUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author lingting 2023-09-27 11:33
 */
internal class RModuleTest {
    @Test
    fun test() {
        I18n.set(Locale.ENGLISH)
        val entity = REntity().setP1("p1").setP2("p2")
        val r = R.ok(entity)
        val json = JacksonUtils.toJson(r)
        Assertions.assertEquals("{\"code\":200,\"data\":{\"p1\":\"p1\",\"p2\":\"p2\"},\"message\":\"Success\"}", json)
        val reference = object : TypeReference<R<REntity>>() {
        }

        val o1 = JacksonUtils
            .toObj("{\"code\":200,\"data\":{\"p1\":\"p1\",\"p2\":\"p2\"},\"message\":\"Success\"}", reference)
        Assertions.assertEquals(200, o1.code)
        Assertions.assertEquals("p1", o1.data!!.p1)

        val o2 = JacksonUtils.toObj("{\"code\":200,\"data\": null,\"message\":\"Success\"}", reference)
        Assertions.assertEquals(200, o2.code)
        Assertions.assertNull(o2.data)

        val o3 = JacksonUtils.toObj("{\"code\":200,\"message\":\"Success\"}", reference)
        Assertions.assertEquals(200, o3.code)
        Assertions.assertNull(o3.data)
    }

    internal class REntity {
        var p1: String? = null
            private set

        var p2: String? = null
            private set

        fun setP1(p1: String?): REntity {
            this.p1 = p1
            return this
        }

        fun setP2(p2: String?): REntity {
            this.p2 = p2
            return this
        }
    }
}
