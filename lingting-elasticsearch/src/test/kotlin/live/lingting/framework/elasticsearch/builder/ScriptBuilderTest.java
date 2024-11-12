package live.lingting.framework.elasticsearch.builder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lingting 2024-03-06 19:55
 */
class ScriptBuilderTest {

	@Test
	void test() {
		String field = "userId";
		assertEquals("ctx._source.userId = null", ScriptBuilder.genSetNull(field));
		assertEquals("ctx._source.userId = params.userId", ScriptBuilder.genSetParams(field));
		assertEquals("if(ctx._source.userId==null || ctx._source.userId==''){ctx._source.userId = params.userId;}",
				ScriptBuilder.genSetIfAbsent(field));
		assertEquals("ctx._source.userId -= params.userId;",
				ScriptBuilder.builder().decrease("userId").build().source());
	}

}
