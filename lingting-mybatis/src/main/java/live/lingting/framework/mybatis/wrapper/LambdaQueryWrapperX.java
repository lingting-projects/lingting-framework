package live.lingting.framework.mybatis.wrapper;

import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;
import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.conditions.SharedString;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import live.lingting.framework.util.CollectionUtils;
import live.lingting.framework.util.StringUtils;
import net.sf.jsqlparser.expression.Expression;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 增加了一些简单条件的 IfPresent 条件 支持，Collection String Object 等等判断是否为空，或者是否为null
 *
 * @author Hccake 2021/1/14
 * @version 1.0
 */
public class LambdaQueryWrapperX<T> extends AbstractLambdaWrapper<T, LambdaQueryWrapperX<T>>
	implements Query<LambdaQueryWrapperX<T>, T, SFunction<T, ?>> {

	/**
	 * 查询字段
	 */
	private SharedString sqlSelect = new SharedString();

	/**
	 * 不建议直接 new 该实例，使用 WrappersX.lambdaQueryX(entity)
	 */
	public LambdaQueryWrapperX() {
		this((T) null);
	}

	/**
	 * 不建议直接 new 该实例，使用 WrappersX.lambdaQueryX(entity)
	 */
	public LambdaQueryWrapperX(T entity) {
		super.setEntity(entity);
		super.initNeed();
	}

	/**
	 * 不建议直接 new 该实例，使用 Wrappers.lambdaQuery(entity)
	 */
	public LambdaQueryWrapperX(Class<T> entityClass) {
		super.setEntityClass(entityClass);
		super.initNeed();
	}

	/**
	 * 不建议直接 new 该实例，使用 Wrappers.lambdaQuery(...)
	 */
	@SuppressWarnings("java:S107")
	LambdaQueryWrapperX(T entity, Class<T> entityClass, SharedString sqlSelect, AtomicInteger paramNameSeq,
						Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments, SharedString lastSql,
						SharedString sqlComment, SharedString sqlFirst) {
		super.setEntity(entity);
		super.setEntityClass(entityClass);
		this.paramNameSeq = paramNameSeq;
		this.paramNameValuePairs = paramNameValuePairs;
		this.expression = mergeSegments;
		this.sqlSelect = sqlSelect;
		this.lastSql = lastSql;
		this.sqlComment = sqlComment;
		this.sqlFirst = sqlFirst;
	}

	/**
	 * SELECT 部分 SQL 设置
	 * @param columns 查询字段
	 */
	@SafeVarargs
	@Override
	public final LambdaQueryWrapperX<T> select(SFunction<T, ?>... columns) {
		if (ArrayUtils.isNotEmpty(columns)) {
			this.sqlSelect.setStringValue(columnsToString(false, columns));
		}
		return typedThis;
	}

	@Override
	public LambdaQueryWrapperX<T> select(boolean condition, List<SFunction<T, ?>> columns) {
		if (condition) {
			this.sqlSelect.setStringValue(columnsToString(false, columns));
		}
		return typedThis;
	}

	/**
	 * 过滤查询的字段信息(主键除外!)
	 * <p>
	 * 例1: 只要 java 字段名以 "test" 开头的 -> select(i -&gt; i.getProperty().startsWith("test"))
	 * </p>
	 * <p>
	 * 例2: 只要 java 字段属性是 CharSequence 类型的 -> select(TableFieldInfo::isCharSequence)
	 * </p>
	 * <p>
	 * 例3: 只要 java 字段没有填充策略的 -> select(i -&gt; i.getFieldFill() == FieldFill.DEFAULT)
	 * </p>
	 * <p>
	 * 例4: 要全部字段 -> select(i -&gt; true)
	 * </p>
	 * <p>
	 * 例5: 只要主键字段 -> select(i -&gt; false)
	 * </p>
	 * @param predicate 过滤方式
	 * @return this
	 */
	@Override
	public LambdaQueryWrapperX<T> select(Class<T> entityClass, Predicate<TableFieldInfo> predicate) {
		if (entityClass == null) {
			entityClass = getEntityClass();
		}
		else {
			setEntityClass(entityClass);
		}
		Assert.notNull(entityClass, "entityClass can not be null");
		this.sqlSelect.setStringValue(TableInfoHelper.getTableInfo(entityClass).chooseSelect(predicate));
		return typedThis;
	}

	@Override
	public String getSqlSelect() {
		return sqlSelect.getStringValue();
	}

	/**
	 * 用于生成嵌套 sql
	 * <p>
	 * 故 sqlSelect 不向下传递
	 * </p>
	 */
	@Override
	protected LambdaQueryWrapperX<T> instance() {
		return new LambdaQueryWrapperX<>(getEntity(), getEntityClass(), null, paramNameSeq, paramNameValuePairs,
			new MergeSegments(), SharedString.emptyString(), SharedString.emptyString(),
			SharedString.emptyString());
	}

	@Override
	public void clear() {
		super.clear();
		sqlSelect.toNull();
	}

	// ======= 分界线，以上 copy 自 mybatis-plus 源码 =====

	/**
	 * 当前条件只是否非null，且不为空
	 * @param obj 值
	 * @return boolean 不为空返回true
	 */
	protected boolean isPresent(Object obj) {
		if (null == obj) {
			return false;
		}
		else if (obj instanceof CharSequence charSequence) {
			// 字符串比较特殊，如果是空字符串也不行
			return StringUtils.hasText(charSequence);
		}
		else if (obj instanceof Collection<?> collection) {
			return !CollectionUtils.isEmpty(collection);
		}
		if (obj.getClass().isArray()) {
			return ArrayUtils.isNotEmpty((Object[]) obj);
		}
		return true;
	}

	public LambdaQueryWrapperX<T> eqIfPresent(SFunction<T, ?> column, Object val) {
		return super.eq(isPresent(val), column, val);
	}

	public LambdaQueryWrapperX<T> neIfPresent(SFunction<T, ?> column, Object val) {
		return super.ne(isPresent(val), column, val);
	}

	public LambdaQueryWrapperX<T> gtIfPresent(SFunction<T, ?> column, Object val) {
		return super.gt(isPresent(val), column, val);
	}

	public LambdaQueryWrapperX<T> geIfPresent(SFunction<T, ?> column, Object val) {
		return super.ge(isPresent(val), column, val);
	}

	public LambdaQueryWrapperX<T> ltIfPresent(SFunction<T, ?> column, Object val) {
		return super.lt(isPresent(val), column, val);
	}

	public LambdaQueryWrapperX<T> leIfPresent(SFunction<T, ?> column, Object val) {
		return super.le(isPresent(val), column, val);
	}

	public LambdaQueryWrapperX<T> likeIfPresent(SFunction<T, ?> column, Object val) {
		return super.like(isPresent(val), column, val);
	}

	public LambdaQueryWrapperX<T> notLikeIfPresent(SFunction<T, ?> column, Object val) {
		return super.notLike(isPresent(val), column, val);
	}

	public LambdaQueryWrapperX<T> likeLeftIfPresent(SFunction<T, ?> column, Object val) {
		return super.likeLeft(isPresent(val), column, val);
	}

	public LambdaQueryWrapperX<T> likeRightIfPresent(SFunction<T, ?> column, Object val) {
		return super.likeRight(isPresent(val), column, val);
	}

	public LambdaQueryWrapperX<T> inIfPresent(SFunction<T, ?> column, Object... values) {
		return super.in(isPresent(values), column, values);
	}

	public LambdaQueryWrapperX<T> inIfPresent(SFunction<T, ?> column, Collection<?> values) {
		return super.in(isPresent(values), column, values);
	}

	public LambdaQueryWrapperX<T> notInIfPresent(SFunction<T, ?> column, Object... values) {
		return super.notIn(isPresent(values), column, values);
	}

	public LambdaQueryWrapperX<T> notInIfPresent(SFunction<T, ?> column, Collection<?> values) {
		return super.notIn(isPresent(values), column, values);
	}
	// region customize

	protected LambdaQueryWrapperX<T> appendSqlSegment(ISqlSegment segment) {
		this.appendSqlSegments(() -> "", segment);
		return this;
	}

	public LambdaQueryWrapperX<T> jsonContains(SFunction<T, ?> column, Object... values) {
		return jsonContains(column, Arrays.asList(values));
	}

	public LambdaQueryWrapperX<T> jsonContains(SFunction<T, ?> column, Collection<Object> values) {
		this.appendSqlSegment(() -> {
			String field = this.columnToString(column);
			String keyword = "JSON_CONTAINS";
			String content = values.stream()
				.map(i -> this.formatParam(null, i))
				.collect(Collectors.joining(",", "(", ")"));

			return String.format("%s(%s,JSON_ARRAY%s)", keyword, field, content);
		});
		return this;
	}

	public LambdaQueryWrapperX<T> jsonContainsIfPresent(SFunction<T, ?> column, Object... values) {
		return maybeDo(isPresent(values), () -> jsonContains(column, values));
	}

	public LambdaQueryWrapperX<T> jsonContainsIfPresent(SFunction<T, ?> column, Collection<Object> values) {
		return maybeDo(isPresent(values), () -> jsonContains(column, values));
	}

	public LambdaQueryWrapperX<T> addSql(String sql) {
		return this.appendSqlSegment(() -> sql);
	}

	public LambdaQueryWrapperX<T> addExpression(Expression expression) {
		return this.appendSqlSegment(() -> String.format("(%s)", expression.toString()));
	}

	// endregion

}
