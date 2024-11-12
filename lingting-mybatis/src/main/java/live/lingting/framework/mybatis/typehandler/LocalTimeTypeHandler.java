package live.lingting.framework.mybatis.typehandler;

import live.lingting.framework.util.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * @author lingting 2022/8/22 9:41
 */
public class LocalTimeTypeHandler extends BaseTypeHandler<LocalTime> implements AutoRegisterTypeHandler<LocalTime> {

	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(LocalTimeTypeHandler.class);

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, LocalTime parameter, JdbcType jdbcType)
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
	public LocalTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return parse(rs.getString(columnName));
	}

	@Override
	public LocalTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return parse(rs.getString(columnIndex));
	}

	@Override
	public LocalTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return parse(cs.getString(columnIndex));
	}

	public String format(LocalTime localDate) {
		return localDate.format(FORMATTER);
	}

	public LocalTime parse(String val) {
		if (!StringUtils.hasText(val)) {
			return null;
		}

		try {
			return LocalTime.parse(val, FORMATTER);
		}
		catch (DateTimeParseException e) {
			log.error("Unable to convert string [{}] to LocalTime! using LocalDateTime.toLocalTime", val, e);
			LocalDateTime dateTime = LocalDateTimeTypeHandler.parse(val);
			if (dateTime == null) {
				return null;
			}
			return dateTime.toLocalTime();
		}
	}

}
