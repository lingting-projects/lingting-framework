package live.lingting.framework.jackson

import com.fasterxml.jackson.databind.JsonMappingException
import live.lingting.framework.jackson.JacksonUtils.toJson
import live.lingting.framework.jackson.JacksonUtils.toNode
import live.lingting.framework.jackson.JacksonUtils.toObj
import live.lingting.framework.jackson.JacksonUtils.toXml
import live.lingting.framework.jackson.JacksonUtils.xmlToNode
import live.lingting.framework.util.StringUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author lingting 2023-04-24 19:33
 */
internal class JacksonUtilsTest {

    @Test
    fun test() {
        val nv = toJson(null)
        assertTrue(StringUtils.hasText(nv))
        val entity = Entity("f1", "f2")
        val json = toJson(entity)
        val node = toNode(json)
        assertEquals("f1", node["f1"].asText())
        val obj = toObj(json, Entity::class.java)
        assertEquals("f1", obj.f1)
        assertEquals("f2", obj.f2)

        assertThrows<JsonMappingException>(JsonMappingException::class.java) { toXml(null) }
        val xmlNode = xmlToNode("<root><f1>f1</f1><f2>f2</f2></root>")
        assertEquals("f1", xmlNode["f1"].asText())

        val cs = 500
        val cst = JacksonUtils.convert(cs, String::class)
        assertTrue(cst is String)
        assertEquals(cs.toString(), cst)
    }

    class Entity {
        var f1: String? = null

        var f2: String? = null

        constructor(f1: String?, f2: String?) {
            this.f1 = f1
            this.f2 = f2
        }

        constructor()
    }
}
