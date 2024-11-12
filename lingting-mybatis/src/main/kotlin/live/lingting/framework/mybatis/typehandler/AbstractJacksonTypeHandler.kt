package live.lingting.framework.mybatis.typehandler

import live.lingting.framework.jackson.JacksonUtils
import live.lingting.framework.util.StringUtils
import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

/**
 * @author lingting 2022/9/28 14:43
 */
abstract class AbstractJacksonTypeHandler<T> : BaseTypeHandler<T>() {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    protected fun parse(json: String?): T {
        try {
            if (StringUtils.hasText(json)) {
                return toObject(json)
            }
        } catch (e: Exception) {
            log.error("json to object error! json: {}; message: {}", json, e.message)
        }
        return defaultValue()
    }

    protected fun resolve(obj: T?): String {
        try {
            if (obj != null) {
                return toJson(obj)
            }
        } catch (e: Exception) {
            log.error("object to json error! obj: {}; message: {}", obj, e.message)
        }
        return defaultJson()
    }

    @Throws(SQLException::class)
    override fun setNonNullParameter(ps: PreparedStatement, i: Int, parameter: T, jdbcType: JdbcType) {
        ps.setString(i, resolve(parameter))
    }

    @Throws(SQLException::class)
    override fun getNullableResult(rs: ResultSet, columnName: String): T {
        return parse(rs.getString(columnName))
    }

    @Throws(SQLException::class)
    override fun getNullableResult(rs: ResultSet, columnIndex: Int): T {
        return parse(rs.getString(columnIndex))
    }

    @Throws(SQLException::class)
    override fun getNullableResult(cs: CallableStatement, columnIndex: Int): T {
        return parse(cs.getString(columnIndex))
    }

    /**
     * 从数据库取出的数据转化为对象
     * @param json 数据库存储数据
     * @return 实体类对象
     */
    protected abstract fun toObject(json: String?): T

    /**
     * 将实体类对象转化为数据库存储数据
     * @param t 实体类对象
     * @return 数据库存储数据
     */
    protected fun toJson(t: T): String {
        return JacksonUtils.toJson(t)
    }

    /**
     * 取出数据转化异常时 使用
     * @return 实体类对象
     */
    protected abstract fun defaultValue(): T

    /**
     * 存储数据异常时 使用
     * @return 存储数据
     */
    protected abstract fun defaultJson(): String
}
