package live.lingting.framework.mybatis.typehandler

/**
 * @author lingting 2022/9/28 14:43
 */
abstract class AbstractSetTypeHandler<T> : AbstractJacksonTypeHandler<Set<T>>() {
    override val defaultValue: Set<T>
        get() = HashSet()

    override val defaultJson: String
        get() = "[]"
}
