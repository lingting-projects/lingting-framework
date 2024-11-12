package live.lingting.polaris.grpc.resolver

import com.tencent.polaris.api.rpc.InstancesResponse

interface ResolverInterceptor {
    fun before(context: ResolverContext?)

    fun after(context: ResolverContext?, response: InstancesResponse?): InstancesResponse?

    fun priority(): Int
}
