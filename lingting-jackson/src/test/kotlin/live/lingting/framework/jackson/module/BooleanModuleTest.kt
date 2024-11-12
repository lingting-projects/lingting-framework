package live.lingting.framework.jackson.module

import live.lingting.framework.jackson.JacksonUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-01-29 10:45
 */
internal class BooleanModuleTest {
    @Test
    fun test() {
        // language=JSON
        val json = """
				            {
				  "bt1": true,
				  "bt2": "t",
				  "bt3": 4,
				  "bt4": "4",
				  "bf1": false,
				  "bf2": "f",
				  "bf3": 0,
				  "bf4": "-1",
				  "bn": null
						}

				""".trimIndent()

        val obj = JacksonUtils.toObj(json, Entity::class.java)
        Assertions.assertTrue(obj.bt1!!)
        Assertions.assertTrue(obj.bt2!!)
        Assertions.assertTrue(obj.bt3!!)
        Assertions.assertTrue(obj.bt4!!)
        Assertions.assertFalse(obj.bf1!!)
        Assertions.assertFalse(obj.bf2!!)
        Assertions.assertFalse(obj.bf3!!)
        Assertions.assertFalse(obj.bf4!!)
        Assertions.assertNull(obj.bn)
    }

    internal class Entity {
        var bt1: Boolean? = null

        var bf1: Boolean? = null

        var bt2: Boolean? = null

        var bf2: Boolean? = null

        var bt3: Boolean? = null

        var bf3: Boolean? = null

        var bt4: Boolean? = null

        var bf4: Boolean? = null

        var bn: Boolean? = null
    }
}
