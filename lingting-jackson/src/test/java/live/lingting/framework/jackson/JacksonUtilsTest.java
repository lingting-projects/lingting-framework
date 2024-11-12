package live.lingting.framework.jackson;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import live.lingting.framework.util.StringUtils;
import org.junit.jupiter.api.Test;

import static live.lingting.framework.jackson.JacksonUtils.toJson;
import static live.lingting.framework.jackson.JacksonUtils.toNode;
import static live.lingting.framework.jackson.JacksonUtils.toObj;
import static live.lingting.framework.jackson.JacksonUtils.toXml;
import static live.lingting.framework.jackson.JacksonUtils.xmlToNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

		assertThrows(JsonMappingException.class, () -> toXml(null));
		JsonNode xmlNode = xmlToNode("<root><f1>f1</f1><f2>f2</f2></root>");
		assertEquals("f1", xmlNode.get("f1").asText());
	}

	public static class Entity {

		private String f1;

		private String f2;

		public Entity(String f1, String f2) {
			this.f1 = f1;
			this.f2 = f2;
		}

		public Entity() {}

		public String getF1() {return this.f1;}

		public String getF2() {return this.f2;}

		public void setF1(String f1) {this.f1 = f1;}

		public void setF2(String f2) {this.f2 = f2;}
	}

}
