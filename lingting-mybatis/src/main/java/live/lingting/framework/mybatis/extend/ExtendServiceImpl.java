package live.lingting.framework.mybatis.extend;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * 以前继承 com.baomidou.mybatisplus.extension.service.impl.ServiceImpl 的实现类，现在继承本类
 *
 * @author lingting 2020/7/21 10:00
 */
@SuppressWarnings("unchecked")
public abstract class ExtendServiceImpl<M extends ExtendMapper<T>, T> implements ExtendService<T> {

	protected Log log = LogFactory.getLog(getClass());

	@Setter
	@Getter
	@Resource
	protected M baseMapper;

	@Setter
	@Getter
	@Resource
	protected SqlSessionFactory sessionFactory;

	protected Class<T> entityClass = currentModelClass();

	protected Class<M> mapperClass = currentMapperClass();

	protected Class<M> currentMapperClass() {
		return (Class<M>) ReflectionKit.getSuperClassGenericType(this.getClass(), ExtendServiceImpl.class, 0);
	}

	protected Class<T> currentModelClass() {
		return (Class<T>) ReflectionKit.getSuperClassGenericType(this.getClass(), ExtendServiceImpl.class, 1);
	}

	@Override
	public Class<T> getEntityClass() {
		return entityClass;
	}

	/**
	 * 获取mapperStatementId
	 * @param sqlMethod 方法名
	 * @return 命名id
	 * @since 3.4.0
	 */
	protected String getSqlStatement(SqlMethod sqlMethod) {
		return SqlHelper.getSqlStatement(mapperClass, sqlMethod);
	}

	@Override
	public boolean saveBatch(Collection<T> entityList, int batchSize) {
		String sqlStatement = getSqlStatement(SqlMethod.INSERT_ONE);
		return executeBatch(entityList, batchSize, (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity));
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

}
