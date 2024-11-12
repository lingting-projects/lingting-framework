package live.lingting.framework.mybatis.typehandler

import live.lingting.framework.money.Money
import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import org.apache.ibatis.type.MappedTypes
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

/**
 * mybatis 全局类型处理器
 *
 * @author lingting
 */
@MappedTypes(Money::class)
class MoneyTypeHandler : BaseTypeHandler<Money>(), AutoRegisterTypeHandler<Money?> {
    @Throws(SQLException::class)
    override fun setNonNullParameter(ps: PreparedStatement, i: Int, parameter: Money, jdbcType: JdbcType) {
        ps.setBigDecimal(i, parameter.value)
    }

    @Throws(SQLException::class)
    override fun getNullableResult(rs: ResultSet, columnName: String): Money {
        val decimal = rs.getBigDecimal(columnName)
        return Money.of(decimal)
    }

    @Throws(SQLException::class)
    override fun getNullableResult(rs: ResultSet, columnIndex: Int): Money {
        val decimal = rs.getBigDecimal(columnIndex)
        return Money.of(decimal)
    }

    @Throws(SQLException::class)
    override fun getNullableResult(cs: CallableStatement, columnIndex: Int): Money {
        val decimal = cs.getBigDecimal(columnIndex)
        return Money.of(decimal)
    }
}
