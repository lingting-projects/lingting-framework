package live.lingting.polaris.grpc.util

import com.tencent.polaris.api.core.ConsumerAPI
import com.tencent.polaris.api.pojo.Instance

/**
 * @author [liaochuntao](mailto:liaochuntao@live.com)
 */
class ClientCallInfo(
    val method: String?, val instance: Instance?, val consumerAPI: ConsumerAPI?, val targetNamespace: String?,
    val targetService: String?
) {
    class ClientCallInfoBuilder {
        private var method: String? = null

        private var instance: Instance? = null

        private var consumerAPI: ConsumerAPI? = null

        private var targetNamespace: String? = null

        private var targetService: String? = null

        fun method(method: String?): ClientCallInfoBuilder {
            this.method = method
            return this
        }

        fun instance(instance: Instance?): ClientCallInfoBuilder {
            this.instance = instance
            return this
        }

        fun consumerAPI(consumerAPI: ConsumerAPI?): ClientCallInfoBuilder {
            this.consumerAPI = consumerAPI
            return this
        }

        fun targetNamespace(targetNamespace: String?): ClientCallInfoBuilder {
            this.targetNamespace = targetNamespace
            return this
        }

        fun targetService(targetService: String?): ClientCallInfoBuilder {
            this.targetService = targetService
            return this
        }

        fun build(): ClientCallInfo {
            return ClientCallInfo(method, instance, consumerAPI, targetNamespace, targetService)
        }
    }

    companion object {
        fun builder(): ClientCallInfoBuilder {
            return ClientCallInfoBuilder()
        }
    }
}
