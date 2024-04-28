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
import java.util.HashMap;
import java.util.Map;

/**
 * @author lingting 2022/8/22 9:41
 */
@Slf4j
public class LocalDateTimeTypeHandler extends BaseTypeHandler<LocalDateTime>implements AutoRegisterTypeHandler<LocalDateTime> {

	public static final String MICROSECONDS_DELIMITER = ".";

	public static final String MICROSECONDS = "S";

	public static final String STR_FORMAT_NORMAL = "yyyy-MM-dd HH:mm:ss";

	public static final DateTimeFormatter FORMAT_NORMAL = DateTimeFormatter.ofPattern(STR_FORMAT_NORMAL);

	private static final Map<Integer, DateTimeFormatter> CACHE = new HashMap<>(16);

	public static LocalDateTime parse(String val) {
		if (!StringUtils.hasText(val)) {
			return null;
		}

		// 微秒处理
		if (val.contains(MICROSECONDS_DELIMITER)) {
			int number = val.length() - val.indexOf(MICROSECONDS_DELIMITER) - 1;

			DateTimeFormatter dateTimeFormatter = CACHE.computeIfAbsent(number, k -> {
				String builder = STR_FORMAT_NORMAL + MICROSECONDS_DELIMITER + MICROSECONDS.repeat(Math.max(0, number));
				return DateTimeFormatter.ofPattern(builder);
			});

			return LocalDateTime.parse(val, dateTimeFormatter);
		}

		// 数据类型声明紊乱处理
		try {
			return LocalDateTime.parse(val, FORMAT_NORMAL);
		}
		catch (DateTimeParseException e) {
			log.error("Unable to convert string [{}] to LocalDataTime! using LocalDate.asStartOfDay", val, e);
			// 使用当天0点处理
			return LocalDate.parse(val, LocalDateTypeHandler.FORMATTER).atStartOfDay();
		}
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, LocalDateTime parameter, JdbcType jdbcType)
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
	public LocalDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return parse(rs.getString(columnName));
	}

	@Override
	public LocalDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return parse(rs.getString(columnIndex));
	}

	@Override
	public LocalDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return parse(cs.getString(columnIndex));
	}

	public String format(LocalDateTime localDate) {
		return localDate.format(FORMAT_NORMAL);
	}

}
