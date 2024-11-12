package live.lingting.framework.grpc.properties;

import live.lingting.framework.util.MdcUtils;

import java.time.Duration;

/**
 * @author lingting 2023-04-06 15:18
 */
public class GrpcServerProperties {

	private Integer port;

	private long messageSize = 524288;

	private Duration keepAliveTime = Duration.ofMinutes(30);

	private Duration keepAliveTimeout = Duration.ofSeconds(2);

	private String traceIdKey = MdcUtils.TRACE_ID;

	private int traceOrder = Integer.MIN_VALUE + 100;

	private int exceptionHandlerOrder = Integer.MIN_VALUE + 200;

	public Integer getPort() {return this.port;}

	public long getMessageSize() {return this.messageSize;}

	public Duration getKeepAliveTime() {return this.keepAliveTime;}

	public Duration getKeepAliveTimeout() {return this.keepAliveTimeout;}

	public String getTraceIdKey() {return this.traceIdKey;}

	public int getTraceOrder() {return this.traceOrder;}

	public int getExceptionHandlerOrder() {return this.exceptionHandlerOrder;}

	public void setPort(Integer port) {this.port = port;}

	public void setMessageSize(long messageSize) {this.messageSize = messageSize;}

	public void setKeepAliveTime(Duration keepAliveTime) {this.keepAliveTime = keepAliveTime;}

	public void setKeepAliveTimeout(Duration keepAliveTimeout) {this.keepAliveTimeout = keepAliveTimeout;}

	public void setTraceIdKey(String traceIdKey) {this.traceIdKey = traceIdKey;}

	public void setTraceOrder(int traceOrder) {this.traceOrder = traceOrder;}

	public void setExceptionHandlerOrder(int exceptionHandlerOrder) {this.exceptionHandlerOrder = exceptionHandlerOrder;}
}
