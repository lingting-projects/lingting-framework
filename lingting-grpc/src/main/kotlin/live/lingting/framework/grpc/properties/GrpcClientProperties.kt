package live.lingting.framework.grpc.properties;

import live.lingting.framework.util.MdcUtils;

import java.time.Duration;

/**
 * @author lingting 2023-04-06 15:18
 */
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

	public String getHost() {return this.host;}

	public Integer getPort() {return this.port;}

	public String getTraceIdKey() {return this.traceIdKey;}

	public int getTraceOrder() {return this.traceOrder;}

	public boolean isUsePlaintext() {return this.usePlaintext;}

	public boolean isDisableSsl() {return this.disableSsl;}

	public boolean isEnableRetry() {return this.enableRetry;}

	public boolean isEnableKeepAlive() {return this.enableKeepAlive;}

	public Duration getKeepAliveTime() {return this.keepAliveTime;}

	public Duration getKeepAliveTimeout() {return this.keepAliveTimeout;}

	public void setHost(String host) {this.host = host;}

	public void setPort(Integer port) {this.port = port;}

	public void setTraceIdKey(String traceIdKey) {this.traceIdKey = traceIdKey;}

	public void setTraceOrder(int traceOrder) {this.traceOrder = traceOrder;}

	public void setUsePlaintext(boolean usePlaintext) {this.usePlaintext = usePlaintext;}

	public void setDisableSsl(boolean disableSsl) {this.disableSsl = disableSsl;}

	public void setEnableRetry(boolean enableRetry) {this.enableRetry = enableRetry;}

	public void setEnableKeepAlive(boolean enableKeepAlive) {this.enableKeepAlive = enableKeepAlive;}

	public void setKeepAliveTime(Duration keepAliveTime) {this.keepAliveTime = keepAliveTime;}

	public void setKeepAliveTimeout(Duration keepAliveTimeout) {this.keepAliveTimeout = keepAliveTimeout;}
}
