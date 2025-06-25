package live.lingting.framework.mybatis.typehandler

import live.lingting.framework.data.DataSize
import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import org.apache.ibatis.type.MappedTypes
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * mybatis 全局类型处理器
 * @author lingting
 */
@MappedTypes(DataSize::class)
open class DataSizeTypeHandler : BaseTypeHandler<DataSize>(), AutoRegisterTypeHandler<DataSize> {

    override fun setNonNullParameter(ps: PreparedStatement, i: Int, parameter: DataSize, jdbcType: JdbcType?) {
        ps.setLong(i, parameter.bytes)
    }

    override fun getNullableResult(rs: ResultSet, columnName: String): DataSize? {
        val source = rs.getString(columnName)
        if (source == null) {
            return null
        }
        return DataSize.of(source)
    }

    override fun getNullableResult(rs: ResultSet, columnIndex: Int): DataSize? {
        val source = rs.getString(columnIndex)
        if (source == null) {
            return null
        }
        return DataSize.of(source)
    }

    override fun getNullableResult(cs: CallableStatement, columnIndex: Int): DataSize? {
        val source = cs.getString(columnIndex)
        if (source == null) {
            return null
        }
        return DataSize.of(source)
    }
}
