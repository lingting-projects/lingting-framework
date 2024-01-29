package live.lingting.framework.jackson.module;

import live.lingting.framework.jackson.JacksonUtils;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2024-01-29 10:45
 */
class BooleanModuleTest {

	@Test
	void test() {
		// language=JSON
		String json = """
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
				""";

		Entity obj = JacksonUtils.toObj(json, Entity.class);
		assertTrue(obj.bt1);
		assertTrue(obj.bt2);
		assertTrue(obj.bt3);
		assertTrue(obj.bt4);
		assertFalse(obj.bf1);
		assertFalse(obj.bf2);
		assertFalse(obj.bf3);
		assertFalse(obj.bf4);
		assertNull(obj.bn);
	}

	@Getter
	@Setter
	static class Entity {

		private Boolean bt1;

		private Boolean bf1;

		private Boolean bt2;

		private Boolean bf2;

		private Boolean bt3;

		private Boolean bf3;

		private Boolean bt4;

		private Boolean bf4;

		private Boolean bn;

	}

}
