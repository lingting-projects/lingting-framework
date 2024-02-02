package live.lingting.framework.datascope.parser;

import live.lingting.framework.datascope.DataScope;
import live.lingting.framework.datascope.holder.DataScopeHolder;
import live.lingting.framework.datascope.holder.DataScopeMatchNumHolder;
import live.lingting.framework.datascope.util.SqlParseUtils;
import live.lingting.framework.util.CollectionUtils;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NotExpression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.ParenthesedFromItem;
import net.sf.jsqlparser.statement.select.ParenthesedSelect;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.update.Update;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author lingting 2024-01-19 16:01
 */
public class DefaultDataScopeParser extends DataScopeParser {

	@Override
	protected void insert(Insert insert, int index, String sql) {
		// 不处理
	}

	@Override
	protected void delete(Delete delete, int index, String sql) {
		Expression expression = delete.getWhere();
		Table table = delete.getTable();
		Expression injected = injectExpression(table, expression);
		delete.setWhere(injected);
	}

	@Override
	protected void update(Update update, int index, String sql) {
		Expression expression = update.getWhere();
		Table table = update.getTable();
		Expression injected = injectExpression(table, expression);
		update.setWhere(injected);
	}

	@Override
	protected void select(Select select, int index, String sql) {
		processSelect(select);
		List<WithItem> list = select.getWithItemsList();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(this::processSelect);
		}
	}

	void processSelect(Select select) {
		if (select == null) {
			return;
		}

		// 普通查询
		if (select instanceof PlainSelect plain) {
			processPlainSelect(plain);
		}
		else if (select instanceof ParenthesedSelect parenthesed) {
			processSelect(parenthesed.getSelect());
		}
	}

	void processPlainSelect(PlainSelect plainSelect) {
		// 处理所有查询项中的子查询
		List<SelectItem<?>> selectItems = plainSelect.getSelectItems();
		if (!CollectionUtils.isEmpty(selectItems)) {
			selectItems.forEach(this::processSelectItem);
		}

		// 处理where条件中的查询
		processExpression(plainSelect.getWhere());

		// 处理来源, 获取所有涉及到的表
		List<Table> tables = processFromItem(plainSelect.getFromItem(), true);

		// 处理join
		tables = processJoins(tables, plainSelect.getJoins());

		// 追加where条件
		if (!CollectionUtils.isEmpty(tables)) {
			Expression expression = injectExpression(tables, plainSelect.getWhere());
			plainSelect.setWhere(expression);
		}
	}

	void processSelectItem(SelectItem<?> item) {
		Expression expression = item.getExpression();
		if (expression instanceof Select select) {
			processSelect(select);
		}
	}

	void processExpression(Expression expression) {
		if (expression == null) {
			return;
		}
		if (expression instanceof FromItem item) {
			processDeepFromItem(item);
		}
		boolean isSelect = expression.toString().contains("SELECT");
		// 有子查询
		if (isSelect) {
			// 比较符号 , and , or , 等等
			if (expression instanceof BinaryExpression binary) {
				processExpression(binary.getLeftExpression());
				processExpression(binary.getRightExpression());
			}
			// in
			else if (expression instanceof InExpression in) {
				Expression right = in.getRightExpression();
				if (right instanceof Select select) {
					processSelect(select);
				}
			}
			// exists
			else if (expression instanceof ExistsExpression exists) {
				processExpression(exists.getRightExpression());
			}
			else if (expression instanceof NotExpression not) {
				processExpression(not.getExpression());
			}
			else if (expression instanceof Parenthesis parenthesis) {
				processExpression(parenthesis.getExpression());
			}
		}
	}

	/**
	 * 处理来源项
	 * @param item 来源项
	 * @param isDeep 是否深度处理
	 * @return 查到的表
	 */
	List<Table> processFromItem(FromItem item, boolean isDeep) {
		List<Table> list = new ArrayList<>();
		if (item instanceof Table table) {
			list.add(table);
		}
		else if (item instanceof ParenthesedFromItem pfi) {
			List<Table> lefts = processFromItem(pfi.getFromItem(), isDeep);
			List<Table> tables = processJoins(lefts, pfi.getJoins());
			list.addAll(tables);
		}
		else if (isDeep) {
			processDeepFromItem(item);
		}
		return list;
	}

	void processDeepFromItem(FromItem item) {
		if (item instanceof Select select) {
			processSelect(select);
		}
	}

	@SuppressWarnings({ "java:S3776", "java:S6541" })
	List<Table> processJoins(List<Table> tables, List<Join> joins) {
		if (CollectionUtils.isEmpty(joins)) {
			return tables;
		}

		// 主表
		Table mainTable = null;
		// 左表
		Table leftTable = null;
		if (tables.size() == 1) {
			mainTable = tables.get(0);
			leftTable = mainTable;
		}

		// 对于 on 表达式写在最后的 join，需要记录下前面多个 on 的表名
		Deque<List<Table>> onTableDeque = new LinkedList<>();

		for (Join join : joins) {
			FromItem joinItem = join.getRightItem();

			// 涉及到的表
			List<Table> joinTables = processFromItem(joinItem, false);
			// 没有关联表, 深度处理该项
			if (joinTables.isEmpty()) {
				processDeepFromItem(joinItem);
				leftTable = null;
			}
			// 隐式内连接
			else if (join.isSimple()) {
				tables.addAll(joinTables);
			}
			else {
				// 当前表
				Table joinTable = joinTables.get(0);

				List<Table> onTables = null;
				// 如果不要忽略，且是右连接，则记录下当前表
				if (join.isRight()) {
					mainTable = joinTable;
					if (leftTable != null) {
						onTables = Collections.singletonList(leftTable);
					}
				}
				else if (join.isLeft()) {
					onTables = Collections.singletonList(joinTable);
				}
				// JOIN 等同于 INNER JOIN
				else if (join.isInner() || join.getASTNode().jjtGetFirstToken().toString().equalsIgnoreCase("JOIN")) {
					if (mainTable == null) {
						onTables = Collections.singletonList(joinTable);
					}
					else {
						onTables = Arrays.asList(mainTable, joinTable);
					}
					mainTable = null;
				}

				tables = new ArrayList<>();
				if (mainTable != null) {
					tables.add(mainTable);
				}

				// 获取 join 尾缀的 on 表达式列表
				Collection<Expression> originOnExpressions = join.getOnExpressions();
				// 正常 join on 表达式只有一个，立刻处理
				if (originOnExpressions.size() == 1 && onTables != null) {
					List<Expression> onExpressions = new LinkedList<>();
					Expression injected = injectExpression(onTables, originOnExpressions.iterator().next());
					onExpressions.add(injected);
					join.setOnExpressions(onExpressions);
					leftTable = joinTable;
					continue;
				}
				// 表名压栈，忽略的表压入 null，以便后续不处理
				onTableDeque.push(onTables);
				// 尾缀多个 on 表达式的时候统一处理
				if (originOnExpressions.size() > 1) {
					Collection<Expression> onExpressions = new LinkedList<>();
					for (Expression originOnExpression : originOnExpressions) {
						List<Table> currentTableList = onTableDeque.poll();
						if (CollectionUtils.isEmpty(currentTableList)) {
							onExpressions.add(originOnExpression);
						}
						else {
							Expression injected = injectExpression(currentTableList, originOnExpression);
							onExpressions.add(injected);
						}
					}
					join.setOnExpressions(onExpressions);
				}
				leftTable = joinTable;
			}

		}
		return tables;
	}

	Expression injectExpression(Table table, Expression expression) {
		return injectExpression(Collections.singletonList(table), expression);
	}

	Expression injectExpression(List<Table> tables, Expression expression) {
		List<Expression> list = new ArrayList<>(tables.size());
		// 生成数据权限条件
		for (Table table : tables) {
			// 获取表名
			String tableName = SqlParseUtils.getTableName(table.getName());

			// 进行 dataScope 的表名匹配
			List<DataScope> matchDataScopes = DataScopeHolder.peek()
				.stream()
				.filter(x -> x.includes(tableName))
				.toList();

			// 存在匹配成功的
			if (!CollectionUtils.isEmpty(matchDataScopes)) {
				// 计数
				DataScopeMatchNumHolder.incrementMatchNumIfPresent();
				// 获取到数据权限过滤的表达式
				matchDataScopes.stream()
					.map(x -> x.getExpression(tableName, table.getAlias()))
					.filter(Objects::nonNull)
					.reduce(AndExpression::new)
					.ifPresent(list::add);
			}
		}

		if (list.isEmpty()) {
			return expression;
		}

		Expression inject = list.get(0);

		for (int i = 1; i < list.size(); i++) {
			inject = new AndExpression(inject, list.get(i));
		}

		if (expression == null) {
			return inject;
		}

		Expression left = expression instanceof OrExpression ? new Parenthesis(expression) : expression;

		return new AndExpression(left, inject);
	}

}
