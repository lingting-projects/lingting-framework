package live.lingting.polaris.grpc.interceptor

import com.tencent.polaris.client.api.SDKContext
import io.grpc.ServerInterceptor
import live.lingting.polaris.grpc.server.PolarisGrpcServerBuilder

/**
 * server 侧的拦截器, 优先级优于 ServerInterceptor
 *
 * @author [liaochuntao](mailto:liaochuntao@live.com)
 */
abstract class PolarisServerInterceptor : ServerInterceptor {
    /**
     * server 侧的拦截器，自动注入当前grpc-server与polaris有关的信息
     * @param namespace 服务注册在polaris中的命名空间
     * @param applicationName 当前 grpc-server 进程对应的应用名称，如果调用
     * [PolarisGrpcServerBuilder.applicationName]
     * @param context polaris-sdk的上下文，一个grpc-server进程复用同一个
     */
    abstract fun init(namespace: String?, applicationName: String?, context: SDKContext)
}
