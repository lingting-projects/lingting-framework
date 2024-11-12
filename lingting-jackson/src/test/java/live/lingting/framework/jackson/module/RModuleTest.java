package live.lingting.framework.jackson.module;

import com.fasterxml.jackson.core.type.TypeReference;
import live.lingting.framework.api.R;
import live.lingting.framework.jackson.JacksonUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author lingting 2023-09-27 11:33
 */
class RModuleTest {

	@Test
	void test() {
		REntity entity = new REntity().setP1("p1").setP2("p2");
		R<REntity> r = R.ok(entity);
		String json = JacksonUtils.toJson(r);
		System.out.println(json);
		Assertions.assertEquals("{\"code\":200,\"data\":{\"p1\":\"p1\",\"p2\":\"p2\"},\"message\":\"Success\"}", json);
		TypeReference<R<REntity>> reference = new TypeReference<>() {
		};

		R<REntity> o1 = JacksonUtils
			.toObj("{\"code\":200,\"data\":{\"p1\":\"p1\",\"p2\":\"p2\"},\"message\":\"Success\"}", reference);
		System.out.println(o1);
		Assertions.assertEquals(200, o1.code());
		Assertions.assertEquals("p1", o1.data().getP1());

		R<REntity> o2 = JacksonUtils.toObj("{\"code\":200,\"data\": null,\"message\":\"Success\"}", reference);
		System.out.println(o2);
		Assertions.assertEquals(200, o2.code());
		Assertions.assertNull(o2.data());

		R<REntity> o3 = JacksonUtils.toObj("{\"code\":200,\"message\":\"Success\"}", reference);
		System.out.println(o3);
		Assertions.assertEquals(200, o3.code());
		Assertions.assertNull(o3.data());
	}


	static class REntity {

		private String p1;

		private String p2;

		public String getP1() {return this.p1;}

		public String getP2() {return this.p2;}

		public REntity setP1(String p1) {
			this.p1 = p1;
			return this;
		}

		public REntity setP2(String p2) {
			this.p2 = p2;
			return this;
		}
	}

}
