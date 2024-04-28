package live.lingting.framework.mybatis.typehandler;

import live.lingting.framework.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * @author lingting 2022/8/22 9:41
 */
@Slf4j
public class LocalDateTypeHandler extends BaseTypeHandler<LocalDate> implements AutoRegisterTypeHandler<LocalDate> {

	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, LocalDate parameter, JdbcType jdbcType)
			throws SQLException {
		if (parameter == null) {
			ps.setObject(i, null);
		}
		else if (jdbcType == null) {
			ps.setObject(i, format(parameter));
		}
		else {
			ps.setObject(i, format(parameter), jdbcType.TYPE_CODE);
		}
	}

	@Override
	public LocalDate getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return parse(rs.getString(columnName));
	}

	@Override
	public LocalDate getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return parse(rs.getString(columnIndex));
	}

	@Override
	public LocalDate getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return parse(cs.getString(columnIndex));
	}

	public String format(LocalDate localDate) {
		return localDate.format(FORMATTER);
	}

	public LocalDate parse(String val) {
		if (!StringUtils.hasText(val)) {
			return null;
		}

		try {
			return LocalDate.parse(val, FORMATTER);
		}
		catch (DateTimeParseException e) {
			log.error("Unable to convert string [{}] to LocalData! using LocalDateTime.toLocalData", val, e);
			LocalDateTime dateTime = LocalDateTimeTypeHandler.parse(val);
			if (dateTime == null) {
				return null;
			}
			return dateTime.toLocalDate();
		}
	}

}
