package live.lingting.framework.jackson.sensitive;

import com.fasterxml.jackson.databind.JsonNode;
import live.lingting.framework.jackson.JacksonUtils;
import live.lingting.framework.sensitive.Sensitive;
import live.lingting.framework.sensitive.SensitiveType;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lingting 2023-04-27 15:53
 */
class SensitiveTest {

	@Test
	void sensitive() {
		SensitiveTestEntity test = new SensitiveTestEntity();
		test.setAll("all");
		test.setDefaultValue("default");
		test.setMobile("8677711113333");
		test.setSpi("spi");
		String json = JacksonUtils.toJson(test);
		System.out.println(json);
		JsonNode node = JacksonUtils.toNode(json);
		assertEquals("******", node.get("all").asText());
		assertEquals("d******t", node.get("defaultValue").asText());
		assertEquals("86******33", node.get("mobile").asText());
		assertEquals("*", node.get("spi").asText());
	}

	@Getter
	@Setter
	static class SensitiveTestEntity {

		@Sensitive(SensitiveType.ALL)
		private String all;

		@Sensitive(SensitiveType.DEFAULT)
		private String defaultValue;

		@Sensitive(SensitiveType.MOBILE)
		private String mobile;

		@Sensitive(SensitiveType.CUSTOMER)
		private String spi;

	}

}
