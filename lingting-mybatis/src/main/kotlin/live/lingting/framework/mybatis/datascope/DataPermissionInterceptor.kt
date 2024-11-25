package live.lingting.framework.mybatis.datascope

import java.sql.Connection
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.reflect.KClass
import live.lingting.framework.mybatis.util.PluginUtils
import org.apache.ibatis.executor.statement.StatementHandler
import org.apache.ibatis.mapping.SqlCommandType
import org.apache.ibatis.plugin.Interceptor
import org.apache.ibatis.plugin.Intercepts
import org.apache.ibatis.plugin.Invocation
import org.apache.ibatis.plugin.Plugin
import org.apache.ibatis.plugin.Signature

/**
 * 数据权限拦截器
 *
 */
@Intercepts(Signature(type = StatementHandler::class, method = "prepare", args = [Connection::class, Int::class]))
class DataPermissionInterceptor(
    val factory: JSqlDataScopeParserFactory,
    val scopes: List<JSqlDataScope>,
) : Interceptor {

    companion object {
        /**
         * <p>k: 数据权限类型</p>
         * <p>v: mybatis 的 mappedStatementId</p>
         */
        private val IGNORE_CACHE = ConcurrentHashMap<KClass<out JSqlDataScope>, CopyOnWriteArraySet<String>>()

        @JvmStatic
        fun ignoreAdd(clazz: KClass<out JSqlDataScope>, mappedStatementId: String) {
            IGNORE_CACHE.computeIfAbsent(clazz) { CopyOnWriteArraySet() }.add(mappedStatementId)
        }

        @JvmStatic
        fun ignoreContains(clazz: KClass<out JSqlDataScope>, mappedStatementId: String) = IGNORE_CACHE[clazz]?.contains(mappedStatementId) == true

    }

    override fun intercept(invocation: Invocation): Any {
        val target = invocation.target
        val sh = target as StatementHandler
        val mpSh: PluginUtils.MPStatementHandler = PluginUtils.mpStatementHandler(sh)
        val ms = mpSh.mappedStatement()
        val sct = ms.sqlCommandType
        val mpBs = mpSh.mPBoundSql()
        val mappedStatementId = ms.id

        // 过滤数据权限
        val filter = scopes.filter {
            // 数据权限声明忽略
            if (it.ignore()) {
                return@filter false
            }
            // 没有数据权限匹配当前方法
            if (ignoreContains(it::class, mappedStatementId)) {
                return@filter false
            }
            true
        }

        if (filter.isEmpty()) {
            return invocation.proceed()
        }

        val parser = factory.get(filter)

        // 根据 DataScopes 进行数据权限的 sql 处理
        val result = if (sct == SqlCommandType.SELECT) {
            parser.parserSingle(mpBs.sql())
        } else if (sct == SqlCommandType.INSERT || sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) {
            parser.parserMulti(mpBs.sql())
        } else {
            null
        }
        // 如果sql没有任何数据权限匹配, 则下一次直接跳过
        if (result == null || result.matchNumber < 1) {
            filter.forEach { ignoreAdd(it::class, mappedStatementId) }
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
