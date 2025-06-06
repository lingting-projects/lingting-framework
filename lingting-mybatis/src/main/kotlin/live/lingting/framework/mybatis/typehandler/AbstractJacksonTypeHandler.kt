package live.lingting.framework.mybatis.typehandler

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import live.lingting.framework.util.Slf4jUtils.logger
import live.lingting.framework.util.StringUtils
import org.apache.ibatis.type.JdbcType
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * @author lingting 2022/9/28 14:43
 */
abstract class AbstractJacksonTypeHandler<T> : AbstractTypeHandler<T>() {
    protected val log = logger()

    open val mapper: ObjectMapper
        get() = JacksonTypeHandler.getObjectMapper()

    abstract val reference: TypeReference<T>

    /**
     * 取出数据转化异常时 使用
     * @return 实体类对象
     */
    abstract val defaultValue: T

    /**
     * 存储数据转化异常时 使用
     * @return 存储数据
     */
    abstract val defaultJson: String

    protected fun parse(json: String?): T {
        try {
            if (StringUtils.hasText(json)) {
                return toObject(json!!)
            }
        } catch (e: Exception) {
            log.error("json to object error! json: {}; message: {}", json, e.message)
        }
        return defaultValue
    }

    protected fun resolve(obj: T?): String {
        try {
            if (obj != null) {
                return toJson(obj)
            }
        } catch (e: Exception) {
            log.error("object to json error! obj: {}; message: {}", obj, e.message)
        }
        return defaultJson
    }

    override fun setNonNullParameter(ps: PreparedStatement, i: Int, parameter: T?, jdbcType: JdbcType?) {
        ps.setString(i, resolve(parameter))
    }

    override fun getNullableResult(rs: ResultSet, columnName: String): T {
        return parse(rs.getString(columnName))
    }

    override fun getNullableResult(rs: ResultSet, columnIndex: Int): T {
        return parse(rs.getString(columnIndex))
    }

    override fun getNullableResult(cs: CallableStatement, columnIndex: Int): T {
        return parse(cs.getString(columnIndex))
    }

    /**
     * 从数据库取出的数据转化为对象
     * @param json 数据库存储数据
     * @return 实体类对象
     */
    protected open fun toObject(json: String): T {
        return mapper.readValue(json, reference)
    }

    /**
     * 将实体类对象转化为数据库存储数据
     * @param t 实体类对象
     * @return 数据库存储数据
     */
    protected open fun toJson(t: T): String {
        return mapper.writeValueAsString(t)
    }

}
