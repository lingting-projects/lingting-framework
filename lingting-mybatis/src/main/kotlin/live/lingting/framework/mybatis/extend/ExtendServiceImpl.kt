package live.lingting.framework.mybatis.extend

import com.baomidou.mybatisplus.core.enums.SqlMethod
import com.baomidou.mybatisplus.core.toolkit.Constants
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper
import live.lingting.framework.api.PaginationParams
import live.lingting.framework.function.ThrowingFunction
import live.lingting.framework.value.CursorValue
import live.lingting.framework.value.WaitValue
import org.apache.ibatis.binding.MapperMethod.ParamMap
import org.apache.ibatis.logging.Log
import org.apache.ibatis.logging.LogFactory
import org.apache.ibatis.session.ExecutorType
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.TransactionIsolationLevel
import java.io.Serializable
import java.util.function.BiConsumer
import java.util.function.Predicate
import javax.annotation.Resource
import kotlin.math.min

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

    protected var defaultTransactionLevel = ExtendService.defaultTransactionLevel

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

    fun <E> executeBatch(list: Collection<E>, consumer: BiConsumer<SqlSession, E>): Boolean {
        return executeBatch(list, ExtendService.DEFAULT_INSERT_BATCH_SIZE, consumer)
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
    fun <E> executeBatch(list: Collection<E>, batchSize: Int, consumer: BiConsumer<SqlSession, E>): Boolean {
        if (list.isEmpty()) {
            return false
        }
        return useTransactional<Boolean>(ExecutorType.BATCH) {
            val limit = min(list.size, batchSize)
            var i = 0
            for (e in list) {
                consumer.accept(it, e)
                i += 1

                if (i >= limit) {
                    i = 0
                    it.flushStatements()
                }
            }
            true
        }
    }

    override fun <R> useTransactional(
        type: ExecutorType,
        function: ThrowingFunction<SqlSession, R>,
        predicate: Predicate<Throwable>
    ): R {
        return useTransactional(type, defaultTransactionLevel, function, predicate)
    }

    override fun <R> useTransactional(
        type: ExecutorType,
        level: TransactionIsolationLevel,
        function: ThrowingFunction<SqlSession, R>,
        predicate: Predicate<Throwable>
    ): R {
        val session = sessionFactory.openSession(type, level)
        return useTransactional(session, function, predicate)
    }

    override fun <R> useTransactional(
        session: SqlSession,
        function: ThrowingFunction<SqlSession, R>,
        predicate: Predicate<Throwable>
    ): R {
        try {
            val r = function.apply(session)
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

    override fun cursor(params: PaginationParams): CursorValue<T> = mapper.cursor(params)
}
