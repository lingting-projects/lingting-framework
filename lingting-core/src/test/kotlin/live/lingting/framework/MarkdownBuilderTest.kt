package live.lingting.framework

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-04-29 16:35
 */
internal class MarkdownBuilderTest {
    @Test
    fun test() {
        val builder = MarkdownBuilder().title1("title1")
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
            .simpleCode("简易代码")
            .lineBreak()

            .quoteBreak("quote")

            .code("shell", "cd ~")
            .json("[1,2,3]")

        val string = builder.build()
        Assertions.assertFalse(string.isBlank())
        Assertions.assertTrue(string.contains("####"))
        Assertions.assertTrue(string.contains("-"))
        Assertions.assertTrue(string.contains("```"))
        Assertions.assertEquals(21, builder.lines())
    }
}
