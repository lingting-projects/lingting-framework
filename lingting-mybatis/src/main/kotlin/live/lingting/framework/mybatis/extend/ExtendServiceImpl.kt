package live.lingting.framework.mybatis.extend

import com.baomidou.mybatisplus.core.enums.SqlMethod
import com.baomidou.mybatisplus.core.toolkit.Constants
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper
import java.io.Serializable
import java.util.function.BiConsumer
import java.util.function.Predicate
import javax.annotation.Resource
import live.lingting.framework.api.PaginationParams
import live.lingting.framework.function.ThrowingSupplier
import live.lingting.framework.value.WaitValue
import org.apache.ibatis.binding.MapperMethod.ParamMap
import org.apache.ibatis.logging.Log
import org.apache.ibatis.logging.LogFactory
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory

/**
 * 以前继承 com.baomidou.mybatisplus.extension.service.impl.ServiceImpl 的实现类，现在继承本类
 * @author lingting 2020/7/21 10:00
 */
@Suppress("UNCHECKED_CAST")
abstract class ExtendServiceImpl<M : ExtendMapper<T>, T> : ExtendService<T> {

    protected val log: Log = LogFactory.getLog(javaClass)

    private val mapperValue = WaitValue.of<M>()

    var mapper: M
        get() = mapperValue.notNull()
        @Resource
        set(value) {
            mapperValue.update(value)
        }

    private val sessionFactoryValue = WaitValue.of<SqlSessionFactory>()

    var sessionFactory: SqlSessionFactory
        get() = sessionFactoryValue.notNull()
        @Resource
        set(value) {
            sessionFactoryValue.update(value)
        }

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
     * @param sqlMethod 方法名
     * @return 命名id
     * @since 3.4.0
     */
    protected fun getSqlStatement(sqlMethod: SqlMethod): String {
        return SqlHelper.getSqlStatement(mapperClass, sqlMethod)
    }

    override fun toIpage(params: PaginationParams): Page<T> {
        return mapper.toIPage(params)
    }

    override fun save(entity: T): Boolean {
        return SqlHelper.retBool(mapper.insert(entity))
    }

    override fun saveBatch(entityList: Collection<T>, batchSize: Int): Boolean {
        val sqlStatement = getSqlStatement(SqlMethod.INSERT_ONE)
        return executeBatch(entityList, batchSize) { sqlSession, entity -> sqlSession.insert(sqlStatement, entity) }
    }

    override fun saveIgnore(t: T) {
        mapper.insertIgnore(t)
    }

    override fun saveIgnoreBatch(collection: Collection<T>) {
        saveIgnoreBatch(collection, ExtendService.DEFAULT_INSERT_BATCH_SIZE)
    }

    override fun saveIgnoreBatch(collection: Collection<T>, batchSize: Int) {
        executeBatch(collection, batchSize) { session, e ->
            val m = session.getMapper(mapperClass)
            m.insertIgnore(e)
        }
    }

    override fun removeById(id: Serializable): Boolean {
        return SqlHelper.retBool(mapper.deleteById(id))
    }

    override fun removeByIds(idList: Collection<Serializable>): Boolean {
        if (idList.isEmpty()) {
            return false
        }
        return SqlHelper.retBool(mapper.deleteByIds(idList))
    }

    override fun updateById(entity: T): Boolean {
        return SqlHelper.retBool(mapper.updateById(entity))
    }

    override fun updateBatchById(entityList: Collection<T>, batchSize: Int): Boolean {
        val sqlStatement = getSqlStatement(SqlMethod.UPDATE_BY_ID)
        return executeBatch(entityList, batchSize) { sqlSession, entity ->
            val param = ParamMap<T>()
            param[Constants.ENTITY] = entity
            sqlSession.update(sqlStatement, param)
        }
    }

    override fun getById(id: Serializable): T? {
        return mapper.selectById(id)
    }

    override fun listByIds(ids: Collection<Serializable>): List<T> {
        return mapper.selectBatchIds(ids)
    }

    override fun list(): List<T> {
        return mapper.selectList(null)
    }

    /**
     * 执行批量操作
     * @param list      数据集合
     * @param batchSize 批量大小
     * @param consumer  执行方法
     * @param <E>       泛型
     * @return 操作结果
     * @since 3.3.1
    </E> */
    protected fun <E> executeBatch(list: Collection<E>, batchSize: Int, consumer: BiConsumer<SqlSession, E>): Boolean {
        return useTransactional<Boolean>(ThrowingSupplier {
            SqlHelper.executeBatch(sessionFactory, this.log, list, batchSize, consumer)
        })
    }

    override fun <R> useTransactional(supplier: ThrowingSupplier<R>, predicate: Predicate<Throwable>): R {
        val session = sessionFactory.openSession(false)
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

}
