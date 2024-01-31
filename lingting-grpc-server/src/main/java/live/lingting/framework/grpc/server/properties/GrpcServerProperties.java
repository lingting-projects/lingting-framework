package live.lingting.framework.grpc.server.properties;

import live.lingting.framework.util.MdcUtils;
import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * @author lingting 2023-04-06 15:18
 */
@Data
public class GrpcServerProperties {

	private Integer port;

	private String traceIdKey = MdcUtils.TRACE_ID;

	private long messageSize = 524288;

	/**
	 * 单位: 毫秒
	 */
	private long keepAliveTime = TimeUnit.HOURS.toMillis(2);

	/**
	 * 单位: 毫秒
	 */
	private long keepAliveTimeout = TimeUnit.SECONDS.toMillis(20);

}
