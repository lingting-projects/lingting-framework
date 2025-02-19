package live.lingting.framework.system

import java.nio.charset.Charset
import live.lingting.framework.util.SystemUtils

/**
 * @author lingting 2022/6/25 11:55
 */
open class Command protected constructor(init: String, enter: String, exit: String, charset: Charset) :
    AbstractCommand<Command>(init, enter, exit, charset) {

    companion object {

        /**
         * 获取命令操作实例. 此实例默认使用系统字符集, 如果发现部分带非英文字符和特殊符号命令执行异常, 建议使用
         * [Command.of] 自定义对应的字符集
         * @param init 初始命令
         */
        @JvmStatic
        fun of(init: String): Command = of(init, SystemUtils.charset())

        /**
         * 推荐使用此实例
         */
        @JvmStatic
        fun of(init: String, charset: Charset): Command {
            return of(init, ENTER, EXIT, charset)
        }

        @JvmStatic
        fun of(init: String, enter: String, exit: String, charset: Charset): Command {
            return Command(init, enter, exit, charset)
        }

    }

    public override fun write(bytes: ByteArray): Command {
        return super.write(bytes)
    }

}
