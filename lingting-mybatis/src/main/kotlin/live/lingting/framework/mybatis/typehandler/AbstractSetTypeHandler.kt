package live.lingting.framework.mybatis.typehandler

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author lingting 2022/9/28 14:43
 */
abstract class AbstractSetTypeHandler<T> : AbstractJacksonTypeHandler<Set<T>>() {
    /**
     * 取出数据转化异常时 使用
     *
     * @return 实体类对象
     */
    override fun defaultValue(): Set<T> {
        return HashSet()
    }

    /**
     * 存储数据异常时 使用
     *
     * @return 存储数据
     */
    override fun defaultJson(): String {
        return "[]"
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(AbstractSetTypeHandler::class.java)
    }
}
