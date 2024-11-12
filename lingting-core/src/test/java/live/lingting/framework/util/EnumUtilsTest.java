package live.lingting.framework.util;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lingting 2024-01-29 13:51
 */
class EnumUtilsTest {

	@Test
	void test() {
		assertEquals(1, EnumUtils.getValue(IE.IE1));
		assertEquals(2, EnumUtils.getValue(IE.IE2));

		assertEquals(1, EnumUtils.getValue(JE.JE1));
		assertEquals(2, EnumUtils.getValue(JE.JE2));
	}

	enum IE implements IEnum<Integer> {

		IE1(1), IE2(2),

		;

		private final Integer value;

		private IE(Integer value) {
			this.value = value;
		}

		public Integer getValue() {return this.value;}
	}

	enum JE {

		JE1(1), JE2(2),

		;

		@JsonValue
		private final Integer value;

		private JE(Integer value) {
			this.value = value;
		}

		public Integer getValue() {return this.value;}
	}

}
