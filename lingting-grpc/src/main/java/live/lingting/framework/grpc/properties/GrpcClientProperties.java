package live.lingting.framework.grpc.properties;

import live.lingting.framework.util.MdcUtils;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

/**
 * @author lingting 2023-04-06 15:18
 */
@Getter
@Setter
public class GrpcClientProperties {

	private String host;

	private Integer port = 80;

	private String traceIdKey = MdcUtils.TRACE_ID;

	private int traceOrder = Integer.MIN_VALUE + 100;

	private boolean usePlaintext = false;

	/**
	 * 是否关闭ssl校验,仅在不使用明文时生效
	 */
	private boolean disableSsl = false;

	private boolean enableRetry = true;

	private boolean enableKeepAlive = true;

	private Duration keepAliveTime = Duration.ofMinutes(30);

	private Duration keepAliveTimeout = Duration.ofSeconds(2);

}
