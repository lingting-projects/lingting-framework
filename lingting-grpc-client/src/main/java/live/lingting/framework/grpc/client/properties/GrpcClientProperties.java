package live.lingting.framework.grpc.client.properties;

import live.lingting.framework.util.MdcUtils;
import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * @author lingting 2023-04-06 15:18
 */
@Data
public class GrpcClientProperties {

	private String host;

	private Integer port = 80;

	private String traceIdKey = MdcUtils.TRACE_ID;

	private boolean usePlaintext = false;

	/**
	 * 是否关闭ssl校验,仅在不使用明文时生效
	 */
	private boolean disableSsl = false;

	private boolean enableRetry = true;

	private boolean enableKeepAlive = true;

	/**
	 * 单位: 毫秒
	 */
	private long keepAliveTime = TimeUnit.MINUTES.toMillis(30);

	/**
	 * 单位: 毫秒
	 */
	private long keepAliveTimeout = TimeUnit.SECONDS.toMillis(2);

}
