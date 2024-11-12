package live.lingting.framework.jackson

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import live.lingting.framework.util.StringUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author lingting 2023-04-24 19:33
 */
internal class JacksonUtilsTest {
    @Test
    fun test() {
        assertTrue(StringUtils.hasText(toJson(null)))
        val entity = Entity("f1", "f2")
        val json: String = toJson(entity)
        val node: JsonNode = toNode(json)
        Assertions.assertEquals("f1", node["f1"].asText())
        val obj: Entity = toObj(json, Entity::class.java)
        Assertions.assertEquals("f1", obj.f1)
        Assertions.assertEquals("f2", obj.f2)

        Assertions.assertThrows<JsonMappingException>(JsonMappingException::class.java) { toXml(null) }
        val xmlNode: JsonNode = xmlToNode("<root><f1>f1</f1><f2>f2</f2></root>")
        Assertions.assertEquals("f1", xmlNode["f1"].asText())
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
