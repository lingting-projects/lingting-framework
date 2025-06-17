package live.lingting.framework.jackson.module

import com.fasterxml.jackson.core.type.TypeReference
import live.lingting.framework.data.DataSize
import live.lingting.framework.jackson.JacksonUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author lingting 2025/4/9 16:19
 */
class DataSizeModuleTest {

    @Test
    fun test() {
        val g1 = 1073741824L
        val size = DataSize.ofGb(1)

        assertEquals(g1, size.bytes)

        // language=JSON
        val json = """
            {"v1":1,"v2":1073741824}
        """.trimIndent()

        val map = JacksonUtils.toObj(json, object : TypeReference<Map<String, DataSize>>() {})
        assertEquals(DataSize.ofBytes(1), map["v1"])
        assertEquals(size, map["v2"])
        val toJson = JacksonUtils.toJson(map)
        assertEquals(json, toJson)
    }
}
