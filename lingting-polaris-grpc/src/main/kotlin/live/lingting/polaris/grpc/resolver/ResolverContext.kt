package live.lingting.polaris.grpc.resolver

import com.tencent.polaris.api.pojo.ServiceKey
import com.tencent.polaris.client.api.SDKContext
import java.net.URI

class ResolverContext {
    var targetUri: URI? = null
        private set

    var context: SDKContext? = null
        private set

    var sourceService: ServiceKey? = null
        private set

    class ResolverContextBuilder {
        private var targetUri: URI? = null

        private var context: SDKContext? = null

        private var sourceService: ServiceKey? = null

        fun targetUri(targetUri: URI?): ResolverContextBuilder {
            this.targetUri = targetUri
            return this
        }

        fun context(context: SDKContext?): ResolverContextBuilder {
            this.context = context
            return this
        }

        fun sourceService(sourceService: ServiceKey?): ResolverContextBuilder {
            this.sourceService = sourceService
            return this
        }

        fun build(): ResolverContext {
            val resolverContext = ResolverContext()
            resolverContext.context = this.context
            resolverContext.targetUri = this.targetUri
            resolverContext.sourceService = this.sourceService
            return resolverContext
        }
    }

    companion object {
        fun builder(): ResolverContextBuilder {
            return ResolverContextBuilder()
        }
    }
}
