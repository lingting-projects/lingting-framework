package live.lingting.framework.mybatis.extend;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import live.lingting.framework.api.ApiResultCode;
import live.lingting.framework.api.PaginationParams;
import live.lingting.framework.exception.BizException;
import live.lingting.framework.function.ThrowingRunnable;
import live.lingting.framework.function.ThrowingSupplier;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 以前继承 com.baomidou.mybatisplus.extension.service.IService 的实现类，现在继承当前类
 *
 * @author lingting 2020/7/21 9:58
 */
public interface ExtendService<T> {

	/**
	 * 默认批次提交数量
	 */
	int DEFAULT_BATCH_SIZE = 1000;

	/**
	 * 默认一次批量插入的数量
	 */
	int DEFAULT_INSERT_BATCH_SIZE = 5000;

	Page<T> toIpage(PaginationParams params);

	/**
	 * 插入一条记录（选择字段，策略插入）
	 * @param entity 实体对象
	 */
	boolean save(T entity);

	/**
	 * 插入（批量）
	 * @param entityList 实体对象集合
	 */
	default boolean saveBatch(Collection<T> entityList) {
		return useTransactional(() -> saveBatch(entityList, DEFAULT_INSERT_BATCH_SIZE));
	}

	/**
	 * 插入（批量）
	 * @param entityList 实体对象集合
	 * @param batchSize 插入批次数量
	 */
	boolean saveBatch(Collection<T> entityList, int batchSize);

	/**
	 * 根据 ID 删除
	 * @param id 主键ID
	 */
	boolean removeById(Serializable id);

	/**
	 * 删除（根据ID 批量删除）
	 * @param idList 主键ID列表
	 */
	boolean removeByIds(Collection<? extends Serializable> idList);

	/**
	 * 根据 ID 选择修改
	 * @param entity 实体对象
	 */
	boolean updateById(T entity);

	/**
	 * 根据ID 批量更新
	 * @param entityList 实体对象集合
	 */
	default boolean updateBatchById(Collection<T> entityList) {
		return useTransactional(() -> updateBatchById(entityList, DEFAULT_BATCH_SIZE));
	}

	/**
	 * 根据ID 批量更新
	 * @param entityList 实体对象集合
	 * @param batchSize 更新批次数量
	 */
	boolean updateBatchById(Collection<T> entityList, int batchSize);

	default T getExist(Serializable id) {
		T t = getById(id);
		if (t == null) {
			throw new BizException(ApiResultCode.GET_ERROR);
		}
		return t;
	}

	/**
	 * 根据 ID 查询
	 * @param id 主键ID
	 */
	T getById(Serializable id);

	/**
	 * 查询（根据ID 批量查询）
	 * @param idList 主键ID列表
	 */
	List<T> listByIds(Collection<? extends Serializable> idList);

	/**
	 * 查询所有
	 */
	List<T> list();

	/**
	 * 获取 entity 的 class
	 * @return {@link Class<T>}
	 */
	Class<T> getEntityClass();

	// ^^^^^^ Copy From com.baomidou.mybatisplus.extension.service.IService end ^^^^^^

	default <R> R useTransactional(ThrowingSupplier<R> supplier) {
		return useTransactional(supplier, e -> true);
	}

	default void useTransactional(ThrowingRunnable runnable) {
		useTransactional(runnable, e -> true);
	}

	/**
	 * 开启事务. 如果执行异常, 不论是否回滚. 异常均会抛出
	 * @param runnable 在事务中运行
	 * @param predicate 消费异常, 返回true表示回滚事务
	 */
	default void useTransactional(ThrowingRunnable runnable, Predicate<Throwable> predicate) {
		useTransactional(() -> {
			runnable.run();
			return null;
		}, predicate);
	}

	<R> R useTransactional(ThrowingSupplier<R> supplier, Predicate<Throwable> predicate);

	default void fallback(Runnable runnable, Function<Exception, RuntimeException> fallback) {
		fallback(() -> {
			runnable.run();
			return true;
		}, fallback);
	}

	default void fallback(BooleanSupplier supplier, Function<Exception, RuntimeException> fallback) {
		boolean flag;
		Exception e = null;
		try {
			flag = supplier.getAsBoolean();
		}
		catch (Exception ex) {
			flag = false;
			e = ex;
		}

		if (!flag) {
			RuntimeException exception = fallback.apply(e);
			if (exception != null) {
				throw exception;
			}
		}
	}

	/**
	 * 保存降级, 在保存失败时执行降级方法
	 * @param t 保存的数据
	 */
	default void saveFallback(T t) {
		saveFallback(t, e -> new BizException(ApiResultCode.SAVE_ERROR, e));
	}

	/**
	 * 保存降级, 在保存失败时执行降级方法
	 * @param t 保存的数据
	 * @param fallback 自定义降级处理
	 */
	default void saveFallback(T t, Function<Exception, RuntimeException> fallback) {
		fallback(() -> save(t), fallback);
	}

	/**
	 * 更新降级, 在更新失败时执行降级方法
	 * @param t 更新的数据
	 */
	default void updateFallback(T t) {
		updateFallback(t, e -> new BizException(ApiResultCode.UPDATE_ERROR, e));
	}

	/**
	 * 更新降级, 在更新失败时执行降级方法
	 * @param t 更新的数据
	 * @param fallback 自定义降级处理
	 */
	default void updateFallback(T t, Function<Exception, RuntimeException> fallback) {
		fallback(() -> updateById(t), fallback);
	}

}
