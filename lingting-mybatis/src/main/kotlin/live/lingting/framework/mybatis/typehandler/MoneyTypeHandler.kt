package live.lingting.framework.mybatis.typehandler

import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet
import live.lingting.framework.money.Money
import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import org.apache.ibatis.type.MappedTypes

/**
 * mybatis 全局类型处理器
 *
 * @author lingting
 */
@MappedTypes(Money::class)
class MoneyTypeHandler : BaseTypeHandler<Money>(), AutoRegisterTypeHandler<Money> {

    override fun setNonNullParameter(ps: PreparedStatement, i: Int, parameter: Money, jdbcType: JdbcType) {
        ps.setBigDecimal(i, parameter.value)
    }


    override fun getNullableResult(rs: ResultSet, columnName: String): Money? {
        val decimal = rs.getBigDecimal(columnName)
        if (decimal == null) {
            return null
        }
        return Money.of(decimal)
    }


    override fun getNullableResult(rs: ResultSet, columnIndex: Int): Money? {
        val decimal = rs.getBigDecimal(columnIndex)
        if (decimal == null) {
            return null
        }
        return Money.of(decimal)
    }


    override fun getNullableResult(cs: CallableStatement, columnIndex: Int): Money? {
        val decimal = cs.getBigDecimal(columnIndex)
        if (decimal == null) {
            return null
        }
        return Money.of(decimal)
    }
}
