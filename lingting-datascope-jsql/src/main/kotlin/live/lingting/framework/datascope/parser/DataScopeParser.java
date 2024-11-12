package live.lingting.framework.datascope.parser;

import live.lingting.framework.datascope.JsqlDataScope;
import live.lingting.framework.datascope.exception.DataScopeException;
import live.lingting.framework.datascope.holder.DataScopeHolder;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.slf4j.Logger;

import java.util.List;

/**
 * @author lingting 2024-01-19 15:48
 */
public abstract class DataScopeParser {

	protected final Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

	public String parser(String sql, List<JsqlDataScope> scopes, boolean isMulti) {
		try {
			DataScopeHolder.push(scopes);
			Statements statements = parser(sql, isMulti);

			StringBuilder builder = new StringBuilder();

			for (int i = 0; i < statements.size(); i++) {
				if (i > 0) {
					builder.append(";");
				}

				Statement statement = statements.get(0);
				String parser = parser(statement, i, sql);
				builder.append(parser);
			}

			return builder.toString();

		}
		finally {
			DataScopeHolder.poll();
		}
	}

	public String parserSingle(String sql, List<JsqlDataScope> scopes) {
		return parser(sql, scopes, false);
	}

	public String parserMulti(String sql, List<JsqlDataScope> scopes) {
		return parser(sql, scopes, true);
	}

	protected Statements parser(String sql, boolean isMulti) {
		try {
			if (isMulti) {
				return CCJSqlParserUtil.parseStatements(sql);
			}
			else {
				Statement statement = CCJSqlParserUtil.parse(sql);
				Statements statements = new Statements();
				statements.add(statement);
				return statements;
			}
		}
		catch (Exception e) {
			throw new DataScopeException("sql parse exception!", e);
		}
	}

	/**
	 * 执行 SQL 解析
	 * @param statement JsqlParser Statement
	 * @return sql
	 */
	protected String parser(Statement statement, int index, String sql) {
		if (log.isDebugEnabled()) {
			log.debug("SQL to parse, SQL: {}", sql);
		}
		if (statement instanceof Insert insert) {
			this.insert(insert, index, sql);
		}
		else if (statement instanceof Select select) {
			this.select(select, index, sql);
		}
		else if (statement instanceof Update update) {
			this.update(update, index, sql);
		}
		else if (statement instanceof Delete delete) {
			this.delete(delete, index, sql);
		}
		sql = statement.toString();
		if (log.isDebugEnabled()) {
			log.debug("parse the finished SQL: {}", sql);
		}
		return sql;
	}

	/**
	 * 新增
	 */
	protected void insert(Insert insert, int index, String sql) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 删除
	 */
	protected void delete(Delete delete, int index, String sql) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 更新
	 */
	protected void update(Update update, int index, String sql) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 查询
	 */
	protected void select(Select select, int index, String sql) {
		throw new UnsupportedOperationException();
	}

}
