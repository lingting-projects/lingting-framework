package live.lingting.framework.mybatis.alias

/**
 * TableAlias 注解没有找到时抛出的异常
 *
 * @author hccake
 * @see TableAlias
 */
class TableAliasNotFoundException : RuntimeException {
    constructor()

    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(cause: Throwable?) : super(cause)
}
