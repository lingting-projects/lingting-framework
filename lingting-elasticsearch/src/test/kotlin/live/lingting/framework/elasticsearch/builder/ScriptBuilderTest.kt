package live.lingting.framework.elasticsearch.builder

import live.lingting.framework.elasticsearch.builder.ScriptBuilder.Companion.builder
import live.lingting.framework.elasticsearch.builder.ScriptBuilder.Companion.genSetIfAbsent
import live.lingting.framework.elasticsearch.builder.ScriptBuilder.Companion.genSetNull
import live.lingting.framework.elasticsearch.builder.ScriptBuilder.Companion.genSetParams
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-03-06 19:55
 */
internal class ScriptBuilderTest {
    @Test
    fun test() {
        val field = "userId"
        Assertions.assertEquals("ctx._source.userId = null", genSetNull(field))
        Assertions.assertEquals("ctx._source.userId = params.userId", genSetParams(field))
        Assertions.assertEquals(
            "if(ctx._source.userId==null || ctx._source.userId==''){ctx._source.userId = params.userId;}",
            genSetIfAbsent(field)
        )
        Assertions.assertEquals(
            "ctx._source.userId -= params.userId;",
            builder<Any>().decrease("userId").build().source()
        )
    }
}
