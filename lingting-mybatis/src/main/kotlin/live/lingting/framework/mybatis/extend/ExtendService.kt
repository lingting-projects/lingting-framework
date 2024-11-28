package live.lingting.framework.mybatis.extend

import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import java.io.Serializable
import java.util.function.BooleanSupplier
import java.util.function.Function
import java.util.function.Predicate
import live.lingting.framework.api.ApiResultCode
import live.lingting.framework.api.PaginationParams
import live.lingting.framework.exception.BizException
import live.lingting.framework.function.ThrowingRunnable
import live.lingting.framework.function.ThrowingSupplier

/**
 * 以前继承 com.baomidou.mybatisplus.extension.service.IService 的实现类，现在继承当前类
 * @author lingting 2020/7/21 9:58
 */
interface ExtendService<T> {

    companion object {
        /**
         * 默认批次提交数量
         */
        const val DEFAULT_BATCH_SIZE: Int = 1000

        /**
         * 默认一次批量插入的数量
         */
        const val DEFAULT_INSERT_BATCH_SIZE: Int = 5000
    }

    fun toIpage(params: PaginationParams): Page<T>

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param entity 实体对象
     */
    fun save(entity: T): Boolean

    /**
     * 插入（批量）
     * @param entityList 实体对象集合
     */
    fun saveBatch(entityList: Collection<T>): Boolean {
        return useTransactional<Boolean>(ThrowingSupplier { saveBatch(entityList, DEFAULT_INSERT_BATCH_SIZE) })
    }

    /**
     * 插入（批量）
     * @param entityList 实体对象集合
     * @param batchSize 插入批次数量
     */
    fun saveBatch(entityList: Collection<T>, batchSize: Int): Boolean

    fun saveIgnore(t: T)

    fun saveIgnoreBatch(collection: Collection<T>)

    fun saveIgnoreBatch(collection: Collection<T>, batchSize: Int)

    /**
     * 根据 ID 删除
     * @param id 主键ID
     */
    fun removeById(id: Serializable): Boolean

    /**
     * 删除（根据ID 批量删除）
     * @param idList 主键ID列表
     */
    fun removeByIds(idList: Collection<Serializable>): Boolean

    /**
     * 根据 ID 选择修改
     * @param entity 实体对象
     */
    fun updateById(entity: T): Boolean

    /**
     * 根据ID 批量更新
     * @param entityList 实体对象集合
     */
    fun updateBatchById(entityList: Collection<T>): Boolean {
        return useTransactional(ThrowingSupplier { updateBatchById(entityList, DEFAULT_BATCH_SIZE) })
    }

    /**
     * 根据ID 批量更新
     * @param entityList 实体对象集合
     * @param batchSize 更新批次数量
     */
    fun updateBatchById(entityList: Collection<T>, batchSize: Int): Boolean

    fun getExist(id: Serializable): T {
        val t: T = getById(id) ?: throw BizException(ApiResultCode.GET_ERROR)
        return t
    }

    /**
     * 根据 ID 查询
     * @param id 主键ID
     */
    fun getById(id: Serializable): T?

    /**
     * 查询（根据ID 批量查询）
     * @param ids 主键ID列表
     */
    fun listByIds(ids: Collection<Serializable>): List<T>

    /**
     * 查询所有
     */
    fun list(): List<T>

    /**
     * 获取 entity 的 class
     * @return [<]
     */
    val entityClass: Class<T>

    // ^^^^^^ Copy From com.baomidou.mybatisplus.extension.service.IService end ^^^^^^
    fun <R> useTransactional(supplier: ThrowingSupplier<R>): R {
        return useTransactional(supplier) { true }
    }

    fun useTransactional(runnable: ThrowingRunnable) {
        return useTransactional(runnable) { true }
    }

    /**
     * 开启事务. 如果执行异常, 不论是否回滚. 异常均会抛出
     * @param runnable 在事务中运行
     * @param predicate 消费异常, 返回true表示回滚事务
     */
    fun useTransactional(runnable: ThrowingRunnable, predicate: Predicate<Throwable>) {
        useTransactional(ThrowingSupplier { runnable.run() }, predicate)
    }

    fun <R> useTransactional(supplier: ThrowingSupplier<R>, predicate: Predicate<Throwable>): R

    fun fallback(runnable: Runnable, fallback: Function<Exception?, RuntimeException?>) {
        fallback(BooleanSupplier {
            runnable.run()
            true
        }, fallback)
    }

    fun fallback(supplier: BooleanSupplier, fallback: Function<Exception?, RuntimeException?>) {
        var flag: Boolean
        var e: Exception? = null
        try {
            flag = supplier.asBoolean
        } catch (ex: Exception) {
            flag = false
            e = ex
        }

        if (!flag) {
            val exception = fallback.apply(e)
            if (exception != null) {
                throw exception
            }
        }
    }

    /**
     * 保存降级, 在保存失败时执行降级方法
     * @param t 保存的数据
     */
    fun saveFallback(t: T) {
        saveFallback(t, Function { BizException(ApiResultCode.SAVE_ERROR, it) })
    }

    /**
     * 保存降级, 在保存失败时执行降级方法
     * @param t 保存的数据
     * @param fallback 自定义降级处理
     */
    fun saveFallback(t: T, fallback: Function<Exception?, RuntimeException?>) {
        fallback(BooleanSupplier { save(t) }, fallback)
    }

    /**
     * 更新降级, 在更新失败时执行降级方法
     * @param t 更新的数据
     */
    fun updateFallback(t: T) {
        saveFallback(t, Function { BizException(ApiResultCode.UPDATE_ERROR, it) })
    }

    /**
     * 更新降级, 在更新失败时执行降级方法
     * @param t 更新的数据
     * @param fallback 自定义降级处理
     */
    fun updateFallback(t: T, fallback: Function<Exception?, RuntimeException?>) {
        fallback(BooleanSupplier { updateById(t) }, fallback)
    }

}
