package live.lingting.framework.mybatis.typehandler

import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * @author lingting 2025/6/6 10:47
 */
abstract class AbstractTypeHandler<T> : BaseTypeHandler<T>() {

    abstract override fun setNonNullParameter(ps: PreparedStatement, i: Int, parameter: T?, jdbcType: JdbcType?)

    abstract override fun getNullableResult(rs: ResultSet, columnName: String): T?

    abstract override fun getNullableResult(rs: ResultSet, columnIndex: Int): T?

    abstract override fun getNullableResult(cs: CallableStatement, columnIndex: Int): T?

}
