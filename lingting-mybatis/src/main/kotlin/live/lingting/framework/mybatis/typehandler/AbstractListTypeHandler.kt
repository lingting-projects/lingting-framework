package live.lingting.framework.mybatis.typehandler

/**
 * @author lingting 2022/9/28 14:43
 */
abstract class AbstractListTypeHandler<T> : AbstractJacksonTypeHandler<List<T>>() {
    override val defaultJson: String
        get() = "[]"

    override val defaultValue: List<T>
        get() = ArrayList()

}
