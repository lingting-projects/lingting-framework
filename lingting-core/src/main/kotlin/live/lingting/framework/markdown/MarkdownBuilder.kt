package live.lingting.framework.markdown

import java.util.regex.Pattern
import kotlin.math.max

/**
 * 生成 markdown 文本
 * @author lingting 2020/6/10 22:43
 */
class MarkdownBuilder {
    /**
     * 存放内容
     */
    protected val content: MutableList<String> = ArrayList()

    /**
     * 当前操作行文本
     */
    protected var lineTextBuilder: StringBuilder

    init {
        this.lineTextBuilder = StringBuilder()
    }

    /**
     * 添加自定义内容
     * @param content 自定义内容
     */
    fun append(content: Any?): MarkdownBuilder {
        lineTextBuilder.append(toString(content))
        return this
    }

    /**
     * 有序列表 自动生成 索引
     * @param content 文本
     */
    fun orderList(content: Any?): MarkdownBuilder {
        // 获取最后一个字符串
        var tmp = ""
        if (!this.content.isEmpty()) {
            tmp = this.content[this.content.size - 1]
        }
        // 索引
        var index = 1

        // 校验 是否 为有序列表行的正则
        val isOrderListPattern = "^\\d\\. .*"
        if (Pattern.matches(isOrderListPattern, tmp)) {
            // 如果是数字开头
            val substring: String = tmp.substring(0, tmp.indexOf(ORDER_LIST_PREFIX) - 1)
            index = substring.toInt()
        }
        return orderList(index, content)
    }

    /**
     * 有序列表
     * @param index 索引
     * @param content 文本
     */
    fun orderList(index: Int, content: Any?): MarkdownBuilder {
        lineBreak()
        lineTextBuilder.append(index).append(ORDER_LIST_PREFIX).append(toString(content))
        return this
    }

    /**
     * 无序列表 - item1 - item2
     */
    fun unorderedList(content: Any?): MarkdownBuilder {
        // 换行
        lineBreak()
        lineTextBuilder.append(UNORDERED_LIST_PREFIX).append(toString(content))
        return this
    }

    /**
     * 图片
     * @param url 图片链接
     */
    fun pic(url: String?): MarkdownBuilder {
        return pic("", url)
    }

    /**
     * 图片
     * @param title 图片标题
     * @param url 图片路径
     */
    fun pic(title: Any?, url: String?): MarkdownBuilder {
        lineTextBuilder.append("![").append(title).append("](").append(url).append(")")
        return this
    }

    /**
     * 链接
     * @param title 标题
     * @param url http 路径
     */
    fun link(title: Any?, url: String?): MarkdownBuilder {
        lineTextBuilder.append("[").append(title).append("](").append(url).append(")")
        return this
    }

    /**
     * 斜体
     */
    fun italic(content: Any?): MarkdownBuilder {
        lineTextBuilder.append(ITALIC_PREFIX).append(toString(content)).append(ITALIC_PREFIX)
        return this
    }

    /**
     * 加粗
     */
    fun bold(content: Any?): MarkdownBuilder {
        lineTextBuilder.append(BOLD_PREFIX).append(toString(content)).append(BOLD_PREFIX)
        return this
    }

    /**
     * 引用 > 文本
     * @param content 文本
     */
    fun quote(vararg content: Any?): MarkdownBuilder {
        lineBreak()
        lineTextBuilder.append(QUOTE_PREFIX)
        for (o in content) {
            lineTextBuilder.append(toString(o))
        }
        return this
    }

    /**
     * 添加引用后, 换行, 写入下一行引用
     */
    fun quoteBreak(vararg content: Any?): MarkdownBuilder {
        // 当前行引用内容
        quote(*content)
        // 空引用行
        return quote()
    }

    /**
     * 代码
     */
    fun code(type: String?, vararg code: Any?): MarkdownBuilder {
        lineBreak()
        lineTextBuilder.append(CODE_PREFIX).append(type)
        lineBreak()
        for (o in code) {
            lineTextBuilder.append(toString(o))
        }
        lineBreak()
        lineTextBuilder.append(CODE_SUFFIX)
        return lineBreak()
    }

    /**
     * 代码
     */
    fun json(json: String): MarkdownBuilder {
        return code("json", json)
    }

    fun simpleCode(content: Any?): MarkdownBuilder {
        return append(CODE_SIMPLE_PREFIX).append(content).append(CODE_SIMPLE_PREFIX)
    }

    /**
     * 强制换行
     */
    fun forceLineBreak(): MarkdownBuilder {
        content.add(lineTextBuilder.toString())
        lineTextBuilder = StringBuilder()
        return this
    }

    /**
     * 换行 当已编辑文本长度不为0时换行
     */
    fun lineBreak(): MarkdownBuilder {
        if (!lineTextBuilder.isEmpty()) {
            return forceLineBreak()
        }
        return this
    }

    /**
     * 生成 i 级标题
     * @author lingting 2020-06-10 22:55:39
     */
    protected fun title(i: Int, content: Any?): MarkdownBuilder {
        // 如果当前操作行已有字符，需要换行
        lineBreak()
        lineTextBuilder.append(TITLE_PREFIX.repeat(max(0, i)))
        this.content.add(lineTextBuilder.append(" ").append(toString(content)).toString())
        lineTextBuilder = StringBuilder()
        return this
    }

    fun title1(text: Any?): MarkdownBuilder {
        return title(1, text)
    }

    fun title2(text: Any?): MarkdownBuilder {
        return title(2, text)
    }

    fun title3(text: Any?): MarkdownBuilder {
        return title(3, text)
    }

    fun title4(text: Any?): MarkdownBuilder {
        return title(4, text)
    }

    fun title5(text: Any?): MarkdownBuilder {
        return title(5, text)
    }

    fun toString(o: Any?): String {
        if (o == null) {
            return ""
        }
        return o.toString()
    }

    override fun toString(): String {
        return build()
    }

    fun lines(): Int {
        val size = content.size
        return if (lineTextBuilder.isEmpty()) size else size + 1
    }

    /**
     * 构筑 Markdown 文本
     */
    fun build(): String {
        lineBreak()
        val res = StringBuilder()
        content.forEach { line -> res.append(line).append(" \n") }
        return res.toString()
    }

    companion object {
        const val TITLE_PREFIX: String = "#"

        const val QUOTE_PREFIX: String = "> "

        const val CODE_PREFIX: String = "``` "

        const val CODE_SUFFIX: String = "```"

        const val BOLD_PREFIX: String = "**"

        const val ITALIC_PREFIX: String = "*"

        const val UNORDERED_LIST_PREFIX: String = "- "

        const val ORDER_LIST_PREFIX: String = ". "

        const val CODE_SIMPLE_PREFIX: String = "`"
    }
}
