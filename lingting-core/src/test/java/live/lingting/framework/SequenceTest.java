package live.lingting.framework;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lingting 2024-01-30 11:38
 */
class SequenceTest {

	@Test
	void test() {
		List<Object> list = new ArrayList<>();
		ES e__1 = new ES(-1);
		ES e_2 = new ES(2);
		list.add(e__1);
		list.add(new ES(1));
		list.add(new ES(1));
		list.add(e_2);

		Sequence.asc(list);
		assertEquals(e__1, list.get(0));
		Sequence.desc(list);
		assertEquals(e_2, list.get(0));

		String e_s = "es";
		list.add(e_s);
		Sequence.asc(list);
		assertEquals(e_s, list.get(list.size() - 1));

		EA e_a = new EA();
		list.add(e_a);
		Sequence.asc(list);
		assertEquals(e_a, list.get(0));

		EO e_o = new EO();
		list.add(e_o);
		Sequence.asc(list);
		assertEquals(e_o, list.get(0));
		Sequence.desc(list);
		assertEquals(e_2, list.get(0));
	}

	@Getter
	@RequiredArgsConstructor
	static class ES implements Sequence {

		private final int sequence;

	}

	@Order(-90)
	static class EA {

	}

	static class EO implements Ordered {

		@Override
		public int getOrder() {
			return -100;
		}

	}

}
