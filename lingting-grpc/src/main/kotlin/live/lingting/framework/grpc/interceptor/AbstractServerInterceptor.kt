package live.lingting.framework.grpc.interceptor;

import io.grpc.ServerInterceptor;
import live.lingting.framework.grpc.GrpcServer;

/**
 * @author lingting 2024-01-31 10:12
 */
public abstract class AbstractServerInterceptor implements ServerInterceptor {

	protected GrpcServer server;

	public void setServer(GrpcServer server) {this.server = server;}
}
