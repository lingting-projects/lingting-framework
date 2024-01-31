package live.lingting.framework.grpc.server.interceptor;

import io.grpc.ServerInterceptor;
import live.lingting.framework.grpc.server.GrpcServer;
import lombok.Setter;

/**
 * @author lingting 2024-01-31 10:12
 */
@Setter
public abstract class AbstractServerInterceptor implements ServerInterceptor {

	protected GrpcServer server;

}
