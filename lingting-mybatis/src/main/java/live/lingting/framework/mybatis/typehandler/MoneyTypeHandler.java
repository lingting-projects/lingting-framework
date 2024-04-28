package live.lingting.framework.mybatis.typehandler;

import live.lingting.framework.money.Money;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * mybatis 全局类型处理器
 *
 * @author lingting
 */
@MappedTypes(Money.class)
public class MoneyTypeHandler extends BaseTypeHandler<Money> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Money parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setBigDecimal(i, parameter.getValue());
	}

	@Override
	public Money getNullableResult(ResultSet rs, String columnName) throws SQLException {
		BigDecimal decimal = rs.getBigDecimal(columnName);
		return Money.of(decimal);
	}

	@Override
	public Money getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		BigDecimal decimal = rs.getBigDecimal(columnIndex);
		return Money.of(decimal);
	}

	@Override
	public Money getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		BigDecimal decimal = cs.getBigDecimal(columnIndex);
		return Money.of(decimal);
	}

}
