package live.lingting.framework.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import live.lingting.framework.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import static live.lingting.framework.jackson.JacksonUtils.toJson;
import static live.lingting.framework.jackson.JacksonUtils.toNode;
import static live.lingting.framework.jackson.JacksonUtils.toObj;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2023-04-24 19:33
 */
class JacksonUtilsTest {

	@Test
	void test() {
		assertTrue(StringUtils.hasText(toJson(null)));
		Entity entity = new Entity("f1", "f2");
		String json = toJson(entity);
		JsonNode node = toNode(json);
		assertEquals("f1", node.get("f1").asText());
		Entity obj = toObj(json, Entity.class);
		assertEquals("f1", obj.f1);
		assertEquals("f2", obj.f2);
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Entity {

		private String f1;

		private String f2;

	}

}
