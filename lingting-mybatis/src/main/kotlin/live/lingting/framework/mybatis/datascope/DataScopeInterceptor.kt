package live.lingting.framework.mybatis.datascope

import live.lingting.framework.datascope.HandlerType
import live.lingting.framework.mybatis.util.PluginUtils
import org.apache.ibatis.executor.statement.StatementHandler
import org.apache.ibatis.mapping.SqlCommandType
import org.apache.ibatis.plugin.Interceptor
import org.apache.ibatis.plugin.Intercepts
import org.apache.ibatis.plugin.Invocation
import org.apache.ibatis.plugin.Plugin
import org.apache.ibatis.plugin.Signature
import java.sql.Connection
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

/**
 * 数据范围拦截器
 */
@Intercepts(
    Signature(
        type = StatementHandler::class,
        method = "prepare",
        args = [Connection::class, Integer::class]
    )
)
class DataScopeInterceptor(
    val factory: JSqlDataScopeParserFactory,
    val scopes: List<JSqlDataScope>,
) : Interceptor {

    companion object {
        /**
         * <p>k: 数据范围类型</p>
         * <p>v: mybatis 的 mappedStatementId</p>
         */
        private val IGNORE_CACHE = ConcurrentHashMap<Class<out JSqlDataScope>, CopyOnWriteArraySet<String>>()

        @JvmStatic
        fun ignoreAdd(clazz: Class<out JSqlDataScope>, mappedStatementId: String) {
            IGNORE_CACHE.computeIfAbsent(clazz) { CopyOnWriteArraySet() }.add(mappedStatementId)
        }

        @JvmStatic
        fun ignoreContains(clazz: Class<out JSqlDataScope>, mappedStatementId: String) =
            IGNORE_CACHE[clazz]?.contains(mappedStatementId) == true

    }

    override fun intercept(invocation: Invocation): Any {
        val target = invocation.target
        val sh = target as StatementHandler
        val mpSh: PluginUtils.MPStatementHandler = PluginUtils.mpStatementHandler(sh)
        val ms = mpSh.mappedStatement()
        val sct = ms.sqlCommandType
        val mpBs = mpSh.mPBoundSql()
        val mappedStatementId = ms.id
        val type: HandlerType?
        val isMulti: Boolean
        when (sct) {
            SqlCommandType.SELECT -> {
                type = HandlerType.QUERY
                isMulti = false
            }

            SqlCommandType.UPDATE -> {
                type = HandlerType.UPDATE
                isMulti = true
            }

            SqlCommandType.DELETE -> {
                type = HandlerType.DELETE
                isMulti = true
            }

            else -> {
                type = null
                isMulti = false
            }
        }

        // 过滤数据范围
        val filter = scopes.filter {
            // 数据范围声明忽略
            if (it.ignore(type)) {
                return@filter false
            }
            // 没有数据范围匹配当前方法
            if (ignoreContains(it::class.java, mappedStatementId)) {
                return@filter false
            }
            true
        }

        if (filter.isEmpty()) {
            return invocation.proceed()
        }

        val parser = factory.get(type, filter)

        // 根据 DataScopes 进行数据范围的 sql 处理
        val result = if (type == null) {
            null
        } else if (isMulti) {
            parser.parserMulti(mpBs.sql())
        } else {
            parser.parserSingle(mpBs.sql())
        }

        // 如果sql没有任何数据范围匹配, 则下一次直接跳过
        if (result == null || result.matchNumber < 1) {
            filter.forEach { ignoreAdd(it::class.java, mappedStatementId) }
        } else {
            mpBs.sql(result.sql)
        }
        // 执行 sql
        return invocation.proceed()
    }

    override fun plugin(target: Any): Any {
        if (target is StatementHandler) {
            return Plugin.wrap(target, this)
        }
        return target
    }
}
