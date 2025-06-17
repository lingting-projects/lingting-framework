package live.lingting.framework.elasticsearch.builder

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-03-06 19:55
 */
internal class ScriptBuilderTest {
    @Test
    fun test() {
        val field = "userId"
        assertEquals("ctx._source.userId = null", ScriptBuilder.genSetNull(field))
        assertEquals("ctx._source.userId = params.userId", ScriptBuilder.genSetParams(field))
        assertEquals(
            "if(ctx._source.userId==null){ctx._source.userId = params.userId;}",
            ScriptBuilder.genSetIfAbsent(field)
        )
        assertEquals(
            "if(ctx._source.userId==null || ctx._source.userId==''){ctx._source.userId = params.userId;}",
            ScriptBuilder.genSetIfBlank(field)
        )
        assertEquals(
            "ctx._source.userId -= params.userId;",
            ScriptBuilder.builder<Any>().decrease("userId").build().source()
        )
    }
}
