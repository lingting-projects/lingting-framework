package live.lingting.framework.mybatis.extend

import com.baomidou.mybatisplus.core.enums.SqlMethod
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils
import com.baomidou.mybatisplus.core.toolkit.Constants
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper
import jakarta.annotation.Resource
import live.lingting.framework.api.PaginationParams
import live.lingting.framework.function.ThrowingSupplier
import org.apache.ibatis.binding.MapperMethod.ParamMap
import org.apache.ibatis.logging.Log
import org.apache.ibatis.logging.LogFactory
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import java.io.Serializable
import java.util.function.BiConsumer
import java.util.function.Predicate

/**
 * 以前继承 com.baomidou.mybatisplus.extension.service.impl.ServiceImpl 的实现类，现在继承本类
 *
 * @author lingting 2020/7/21 10:00
 */
abstract class ExtendServiceImpl<M : ExtendMapper<T>?, T> : ExtendService<T> {
    protected val log: Log = LogFactory.getLog(javaClass)

    protected var mapper: M? = null

    @Resource
    var sessionFactory: SqlSessionFactory? = null

    override var entityClass: Class<T> = currentModelClass()
        protected set

    var mapperClass: Class<M> = currentMapperClass()
        protected set

    protected fun currentMapperClass(): Class<M> {
        return ReflectionKit.getSuperClassGenericType(this.javaClass, ExtendServiceImpl::class.java, 0) as Class<M>
    }

    protected fun currentModelClass(): Class<T> {
        return ReflectionKit.getSuperClassGenericType(this.javaClass, ExtendServiceImpl::class.java, 1) as Class<T>
    }

    /**
     * 获取mapperStatementId
     *
     * @param sqlMethod 方法名
     * @return 命名id
     * @since 3.4.0
     */
    protected fun getSqlStatement(sqlMethod: SqlMethod): String {
        return SqlHelper.getSqlStatement(mapperClass, sqlMethod)
    }

    override fun toIpage(params: PaginationParams): Page<T> {
        return getMapper()!!.toIpage(params)
    }

    override fun save(entity: T): Boolean {
        return SqlHelper.retBool(getMapper()!!.insert(entity))
    }

    override fun saveBatch(entityList: Collection<T>?, batchSize: Int): Boolean {
        val sqlStatement = getSqlStatement(SqlMethod.INSERT_ONE)
        return executeBatch(entityList, batchSize) { sqlSession: SqlSession, entity: T -> sqlSession.insert(sqlStatement, entity) }
    }

    override fun removeById(id: Serializable?): Boolean {
        return SqlHelper.retBool(getMapper()!!.deleteById(id))
    }

    override fun removeByIds(idList: Collection<Serializable?>?): Boolean {
        if (CollectionUtils.isEmpty(idList)) {
            return false
        }
        return SqlHelper.retBool(getMapper()!!.deleteBatchIds(idList))
    }

    override fun updateById(entity: T): Boolean {
        return SqlHelper.retBool(getMapper()!!.updateById(entity))
    }

    override fun updateBatchById(entityList: Collection<T>?, batchSize: Int): Boolean {
        val sqlStatement = getSqlStatement(SqlMethod.UPDATE_BY_ID)
        return executeBatch(entityList, batchSize) { sqlSession: SqlSession, entity: T ->
            val param = ParamMap<T>()
            param[Constants.ENTITY] = entity
            sqlSession.update(sqlStatement, param)
        }
    }

    override fun getById(id: Serializable?): T {
        return getMapper()!!.selectById(id)
    }

    override fun listByIds(idList: Collection<Serializable?>?): List<T> {
        return getMapper()!!.selectBatchIds(idList)
    }

    override fun list(): List<T> {
        return getMapper()!!.selectList(null)
    }

    /**
     * 执行批量操作
     *
     * @param list      数据集合
     * @param batchSize 批量大小
     * @param consumer  执行方法
     * @param <E>       泛型
     * @return 操作结果
     * @since 3.3.1
    </E> */
    protected fun <E> executeBatch(list: Collection<E>?, batchSize: Int, consumer: BiConsumer<SqlSession, E>?): Boolean {
        return useTransactional<Boolean>(ThrowingSupplier<Boolean> { SqlHelper.executeBatch(sessionFactory, this.log, list, batchSize, consumer) })
    }

    override fun <R> useTransactional(supplier: ThrowingSupplier<R>, predicate: Predicate<Throwable?>): R {
        val session = sessionFactory!!.openSession(false)
        try {
            val r = supplier.get()
            session.commit()
            return r
        } catch (e: Exception) {
            // 回滚
            if (predicate.test(e)) {
                session.rollback()
            }
            throw e
        } finally {
            session.close()
        }
    }

    fun getMapper(): M {
        return this.mapper
    }

    fun setMapper(mapper: M) {
        this.mapper = mapper
    }
}
