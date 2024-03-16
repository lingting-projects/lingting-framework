package live.lingting.framework.mybatis.extend;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import jakarta.annotation.Resource;
import live.lingting.framework.api.PaginationParams;
import live.lingting.framework.function.ThrowingSupplier;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * 以前继承 com.baomidou.mybatisplus.extension.service.impl.ServiceImpl 的实现类，现在继承本类
 *
 * @author lingting 2020/7/21 10:00
 */
@SuppressWarnings("unchecked")
public abstract class ExtendServiceImpl<M extends ExtendMapper<T>, T> implements ExtendService<T> {

	protected final Log log = LogFactory.getLog(getClass());

	@Setter
	@Getter
	protected M mapper;

	@Setter
	@Getter
	@Resource
	protected SqlSessionFactory sessionFactory;

	@Getter
	protected Class<T> entityClass = currentModelClass();

	@Getter
	protected Class<M> mapperClass = currentMapperClass();

	protected Class<M> currentMapperClass() {
		return (Class<M>) ReflectionKit.getSuperClassGenericType(this.getClass(), ExtendServiceImpl.class, 0);
	}

	protected Class<T> currentModelClass() {
		return (Class<T>) ReflectionKit.getSuperClassGenericType(this.getClass(), ExtendServiceImpl.class, 1);
	}

	/**
	 * 获取mapperStatementId
	 * @param sqlMethod 方法名
	 * @return 命名id
	 * @since 3.4.0
	 */
	protected String getSqlStatement(SqlMethod sqlMethod) {
		return SqlHelper.getSqlStatement(getMapperClass(), sqlMethod);
	}

	@Override
	public Page<T> toIpage(PaginationParams params) {
		return getMapper().toIpage(params);
	}

	@Override
	public boolean save(T entity) {
		return SqlHelper.retBool(getMapper().insert(entity));
	}

	@Override
	public boolean saveBatch(Collection<T> entityList, int batchSize) {
		String sqlStatement = getSqlStatement(SqlMethod.INSERT_ONE);
		return executeBatch(entityList, batchSize, (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity));
	}

	@Override
	public boolean removeById(Serializable id) {
		return SqlHelper.retBool(getMapper().deleteById(id));
	}

	@Override
	public boolean removeByIds(Collection<? extends Serializable> idList) {
		if (CollectionUtils.isEmpty(idList)) {
			return false;
		}
		return SqlHelper.retBool(getMapper().deleteBatchIds(idList));
	}

	@Override
	public boolean updateById(T entity) {
		return SqlHelper.retBool(getMapper().updateById(entity));
	}

	@Override
	public boolean updateBatchById(Collection<T> entityList, int batchSize) {
		String sqlStatement = getSqlStatement(SqlMethod.UPDATE_BY_ID);
		return executeBatch(entityList, batchSize, (sqlSession, entity) -> {
			MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
			param.put(Constants.ENTITY, entity);
			sqlSession.update(sqlStatement, param);
		});
	}

	@Override
	public T getById(Serializable id) {
		return getMapper().selectById(id);
	}

	@Override
	public List<T> listByIds(Collection<? extends Serializable> idList) {
		return getMapper().selectBatchIds(idList);
	}

	@Override
	public List<T> list() {
		return getMapper().selectList(null);
	}

	/**
	 * 执行批量操作
	 * @param list 数据集合
	 * @param batchSize 批量大小
	 * @param consumer 执行方法
	 * @param <E> 泛型
	 * @return 操作结果
	 * @since 3.3.1
	 */
	protected <E> boolean executeBatch(Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
		return useTransactional(() -> SqlHelper.executeBatch(sessionFactory, this.log, list, batchSize, consumer));
	}

	@Override
	@SneakyThrows
	public <R> R useTransactional(ThrowingSupplier<R> supplier, Predicate<Throwable> predicate) {
		SqlSession session = getSessionFactory().openSession(false);
		try {
			R r = supplier.get();
			session.commit();
			return r;
		}
		catch (Exception e) {
			// 回滚
			if (predicate.test(e)) {
				session.rollback();
			}
			throw e;
		}
		finally {
			session.close();
		}
	}

}
