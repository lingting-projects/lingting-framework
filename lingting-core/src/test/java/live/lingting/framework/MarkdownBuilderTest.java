package live.lingting.framework;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2024-04-29 16:35
 */
class MarkdownBuilderTest {

	@Test
	void test() {
		MarkdownBuilder builder = new MarkdownBuilder().title1("title1")
			.title2("title2")
			.title3("title3")
			.title4("title4")
			.title5("title5")
			.lineBreak()

			.orderList(1, 1)
			.orderList(2, 2)
			.orderList(3, 3)
			.lineBreak()

			.unorderedList(11)
			.unorderedList(12)
			.unorderedList(13)
			.lineBreak()

			.link("url", "https://www.baidu.com")
			.lineBreak()

			.italic("italic")
			.bold("bold")
			.quote("quote")
			.lineBreak()

			.code("shell", "cd ~")
			.json("[1,2,3]")

		;

		String string = builder.toString();
		assertFalse(string.isBlank());
		assertTrue(string.contains("####"));
		assertTrue(string.contains("-"));
		assertTrue(string.contains("```"));

	}

}
