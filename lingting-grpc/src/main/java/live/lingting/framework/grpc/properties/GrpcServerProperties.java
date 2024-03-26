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
public class GrpcServerProperties {

	private Integer port;

	private String traceIdKey = MdcUtils.TRACE_ID;

	private long messageSize = 524288;

	private Duration keepAliveTime = Duration.ofMinutes(30);

	private Duration keepAliveTimeout = Duration.ofSeconds(2);

}
