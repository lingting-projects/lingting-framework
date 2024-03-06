package live.lingting.framework.mybatis.datascope;

import live.lingting.framework.datascope.JsqlDataScope;
import live.lingting.framework.datascope.handler.DataPermissionHandler;
import live.lingting.framework.datascope.holder.DataScopeMatchNumHolder;
import live.lingting.framework.datascope.holder.MappedStatementIdsWithoutDataScope;
import live.lingting.framework.datascope.parser.DataScopeParser;
import live.lingting.framework.mybatis.util.PluginUtils;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

import java.sql.Connection;
import java.util.List;

/**
 * 数据权限拦截器
 *
 * @author Hccake 2020/9/28
 * @version 1.0
 */
@RequiredArgsConstructor
@Intercepts({
		@Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class }) })
public class DataPermissionInterceptor implements Interceptor {

	private final DataScopeParser parser;

	private final DataPermissionHandler handler;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		// 第一版，测试用
		Object target = invocation.getTarget();
		StatementHandler sh = (StatementHandler) target;
		PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
		MappedStatement ms = mpSh.mappedStatement();
		SqlCommandType sct = ms.getSqlCommandType();
		PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
		String mappedStatementId = ms.getId();

		// 获取当前需要控制的 dataScope 集合
		List<JsqlDataScope> filterDataScopes = handler.filterDataScopes(mappedStatementId);
		if (filterDataScopes == null || filterDataScopes.isEmpty()) {
			return invocation.proceed();
		}

		// 根据用户权限判断是否需要拦截，例如管理员可以查看所有，则直接放行
		if (handler.ignorePermissionControl(filterDataScopes, mappedStatementId)) {
			return invocation.proceed();
		}

		// 创建 matchNumTreadLocal
		DataScopeMatchNumHolder.initMatchNum();
		try {
			// 根据 DataScopes 进行数据权限的 sql 处理
			if (sct == SqlCommandType.SELECT) {
				mpBs.sql(parser.parserSingle(mpBs.sql(), filterDataScopes));
			}
			else if (sct == SqlCommandType.INSERT || sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) {
				mpBs.sql(parser.parserMulti(mpBs.sql(), filterDataScopes));
			}
			// 如果解析后发现当前 mappedStatementId 对应的 sql，没有任何数据权限匹配，则记录下来，后续可以直接跳过不解析
			Integer matchNum = DataScopeMatchNumHolder.pollMatchNum();
			List<JsqlDataScope> allDataScopes = handler.dataScopes();
			if (allDataScopes.size() == filterDataScopes.size() && matchNum != null && matchNum == 0) {
				MappedStatementIdsWithoutDataScope.addToWithoutSet(filterDataScopes, mappedStatementId);
			}
		}
		finally {
			DataScopeMatchNumHolder.removeIfEmpty();
		}

		// 执行 sql
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		if (target instanceof StatementHandler) {
			return Plugin.wrap(target, this);
		}
		return target;
	}

}
