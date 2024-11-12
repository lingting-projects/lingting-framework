package live.lingting.framework.jackson.module;

import live.lingting.framework.jackson.JacksonUtils;
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

		public Boolean getBt1() {return this.bt1;}

		public Boolean getBf1() {return this.bf1;}

		public Boolean getBt2() {return this.bt2;}

		public Boolean getBf2() {return this.bf2;}

		public Boolean getBt3() {return this.bt3;}

		public Boolean getBf3() {return this.bf3;}

		public Boolean getBt4() {return this.bt4;}

		public Boolean getBf4() {return this.bf4;}

		public Boolean getBn() {return this.bn;}

		public void setBt1(Boolean bt1) {this.bt1 = bt1;}

		public void setBf1(Boolean bf1) {this.bf1 = bf1;}

		public void setBt2(Boolean bt2) {this.bt2 = bt2;}

		public void setBf2(Boolean bf2) {this.bf2 = bf2;}

		public void setBt3(Boolean bt3) {this.bt3 = bt3;}

		public void setBf3(Boolean bf3) {this.bf3 = bf3;}

		public void setBt4(Boolean bt4) {this.bt4 = bt4;}

		public void setBf4(Boolean bf4) {this.bf4 = bf4;}

		public void setBn(Boolean bn) {this.bn = bn;}
	}

}
