package live.lingting.framework.mybatis.typehandler;

import live.lingting.framework.util.EnumUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author lingting 2022/12/14 16:06
 */
public class EnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> implements AutoRegisterTypeHandler<E> {

	private final Class<E> type;

	public EnumTypeHandler(Class<E> type) {
		this.type = type;
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
		Object value = EnumUtils.getValue(parameter);
		if (jdbcType != null) {
			ps.setObject(i, value, jdbcType.TYPE_CODE);
		}
		else if (value instanceof String) {
			ps.setString(i, value.toString());
		}
		else {
			ps.setObject(i, value);
		}
	}

	@Override
	public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return of(rs.getString(columnName));
	}

	@Override
	public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return of(rs.getString(columnIndex));
	}

	@Override
	public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return of(cs.getString(columnIndex));
	}

	E of(String val) {
		for (E e : type.getEnumConstants()) {
			Object value = EnumUtils.getValue(e);
			if (
				// 值匹配
				Objects.equals(val, value)
					// 字符串值匹配
					|| (value != null && value.toString().equals(val))) {
				return e;
			}
		}
		return null;
	}

}
