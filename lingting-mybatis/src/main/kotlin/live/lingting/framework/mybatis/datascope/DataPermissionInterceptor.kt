package live.lingting.framework.mybatis.datascope

import java.sql.Connection
import live.lingting.framework.datascope.handler.DataPermissionHandler
import live.lingting.framework.datascope.holder.DataScopeMatchNumHolder.Companion.initMatchNum
import live.lingting.framework.datascope.holder.DataScopeMatchNumHolder.Companion.pollMatchNum
import live.lingting.framework.datascope.holder.DataScopeMatchNumHolder.Companion.removeIfEmpty
import live.lingting.framework.datascope.holder.MappedStatementIdsWithoutDataScope
import live.lingting.framework.datascope.parser.DataScopeParser
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
 * @author Hccake 2020/9/28
 * @version 1.0
 */
@Intercepts(Signature(type = StatementHandler::class, method = "prepare", args = [Connection::class, Int::class]))
class DataPermissionInterceptor(private val parser: DataScopeParser, private val handler: DataPermissionHandler) : Interceptor {

    override fun intercept(invocation: Invocation): Any {
        // 第一版，测试用
        val target = invocation.target
        val sh = target as StatementHandler
        val mpSh: PluginUtils.MPStatementHandler = PluginUtils.Companion.mpStatementHandler(sh)
        val ms = mpSh.mappedStatement()
        val sct = ms!!.sqlCommandType
        val mpBs = mpSh.mPBoundSql()
        val mappedStatementId = ms.id

        // 获取当前需要控制的 dataScope 集合
        val filterDataScopes = handler.filterDataScopes(mappedStatementId)
        if (filterDataScopes == null || filterDataScopes.isEmpty()) {
            return invocation.proceed()
        }

        // 根据用户权限判断是否需要拦截，例如管理员可以查看所有，则直接放行
        if (handler.ignorePermissionControl(filterDataScopes, mappedStatementId)) {
            return invocation.proceed()
        }

        // 创建 matchNumTreadLocal
        initMatchNum()
        try {
            // 根据 DataScopes 进行数据权限的 sql 处理
            if (sct == SqlCommandType.SELECT) {
                mpBs.sql(parser.parserSingle(mpBs.sql(), filterDataScopes))
            } else if (sct == SqlCommandType.INSERT || sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) {
                mpBs.sql(parser.parserMulti(mpBs.sql(), filterDataScopes))
            }
            // 如果解析后发现当前 mappedStatementId 对应的 sql，没有任何数据权限匹配，则记录下来，后续可以直接跳过不解析
            val matchNum = pollMatchNum()
            val allDataScopes = handler.dataScopes()
            if (allDataScopes!!.size == filterDataScopes.size && matchNum != null && matchNum == 0) {
                MappedStatementIdsWithoutDataScope.addToWithoutSet(filterDataScopes, mappedStatementId)
            }
        } finally {
            removeIfEmpty()
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
