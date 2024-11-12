package live.lingting.framework.util;

import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author lingting 2022/12/11 20:14
 */
public final class MdcUtils {

	public static final String TRACE_ID = "traceId";

	private MdcUtils() {throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");}

	public static String traceId() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	public static String getTraceId() {
		return MDC.get(TRACE_ID);
	}

	public static String fillTraceId() {
		String traceId = traceId();
		fillTraceId(traceId);
		return traceId;
	}

	public static void fillTraceId(String traceId) {
		MDC.put(TRACE_ID, traceId);
	}

	public static void removeTraceId() {
		MDC.remove(TRACE_ID);
	}

	public static Map<String, String> copyContext() {
		Map<String, String> copy = MDC.getCopyOfContextMap();
		if (copy == null) {
			return new HashMap<>();
		}
		return copy;
	}

}
