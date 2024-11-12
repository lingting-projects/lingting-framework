package live.lingting.framework.jackson.sensitive;

import com.fasterxml.jackson.databind.JsonNode;
import live.lingting.framework.jackson.JacksonUtils;
import live.lingting.framework.sensitive.Sensitive;
import live.lingting.framework.sensitive.serializer.SensitiveAllSerializer;
import live.lingting.framework.sensitive.serializer.SensitiveMobileSerializer;
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

	static class SensitiveTestEntity {

		@Sensitive(SensitiveAllSerializer.class)
		private String all;

		@Sensitive
		private String defaultValue;

		@Sensitive(SensitiveMobileSerializer.class)
		private String mobile;

		@Sensitive(SensitiveSpiProvider.SensitiveSpiSerializer.class)
		private String spi;

		public String getAll() {return this.all;}

		public String getDefaultValue() {return this.defaultValue;}

		public String getMobile() {return this.mobile;}

		public String getSpi() {return this.spi;}

		public void setAll(String all) {this.all = all;}

		public void setDefaultValue(String defaultValue) {this.defaultValue = defaultValue;}

		public void setMobile(String mobile) {this.mobile = mobile;}

		public void setSpi(String spi) {this.spi = spi;}
	}

}
