package live.lingting.framework.system

import java.nio.charset.Charset
import live.lingting.framework.util.SystemUtils

/**
 * @author lingting 2022/6/25 11:55
 */
open class HistoryCommand protected constructor(init: String, enter: String, exit: String, charset: Charset) :
    AbstractCommand<HistoryCommand>(init, enter, exit, charset) {

    companion object {

        /**
         * 获取命令操作实例. 此实例默认使用系统字符集, 如果发现部分带非英文字符和特殊符号命令执行异常, 建议使用
         * [HistoryCommand.of] 自定义对应的字符集
         * @param init 初始命令
         */
        @JvmStatic
        fun of(init: String): HistoryCommand = of(init, SystemUtils.charset())

        /**
         * 推荐使用此实例
         */
        @JvmStatic
        fun of(init: String, charset: Charset): HistoryCommand {
            return of(init, ENTER, EXIT, charset)
        }

        @JvmStatic
        fun of(init: String, enter: String, exit: String, charset: Charset): HistoryCommand {
            return HistoryCommand(init, enter, exit, charset)
        }

    }


    protected val history: MutableList<String> = ArrayList()

    open fun history(): List<String> {
        return history.toList()
    }

    override fun write(str: String): HistoryCommand {
        val bytes = str.toByteArray(charset)
        super.write(bytes)
        if (enter != str) {
            history.add(str)
        }
        return this
    }

    override fun write(bytes: ByteArray): HistoryCommand {
        throw UnsupportedOperationException("Unsupported write from bytes!")
    }

    /**
     * 清空历史记录
     * @return 返回被清除的数据
     */
    open fun cleanHistory(): List<String> {
        val back: List<String> = ArrayList(history)
        history.clear()
        return back
    }

}
