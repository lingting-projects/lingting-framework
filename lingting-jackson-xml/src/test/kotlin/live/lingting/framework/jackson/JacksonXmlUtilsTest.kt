package live.lingting.framework.jackson

import com.fasterxml.jackson.databind.JsonMappingException
import live.lingting.framework.jackson.xml.JacksonXmlUtils.toNode
import live.lingting.framework.jackson.xml.JacksonXmlUtils.toXml
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

/**
 * @author lingting 2023-04-24 19:33
 */
internal class JacksonXmlUtilsTest {

    @Test
    fun test() {
        assertThrows(JsonMappingException::class.java) { toXml(null) }
        val xmlNode = toNode("<root><f1>f1</f1><f2>f2</f2></root>")
        assertEquals("f1", xmlNode["f1"].asText())
    }

}
