package live.lingting.polaris.grpc.util

import com.tencent.polaris.api.pojo.Instance
import com.tencent.polaris.api.pojo.ServiceKey
import io.grpc.Attributes
import io.grpc.Metadata

/**
 * @author [liaochuntao](mailto:liaochuntao@live.com)
 */
class Common private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        val CALLER_SERVICE_KEY: Metadata.Key<String> = Metadata.Key.of(
            "polaris.request.caller.service",
            Metadata.ASCII_STRING_MARSHALLER
        )

        val CALLER_NAMESPACE_KEY: Metadata.Key<String> = Metadata.Key.of(
            "polaris.request.caller.namespace",
            Metadata.ASCII_STRING_MARSHALLER
        )

        /**
         * [io.grpc.Attributes] 中存放 [Instance] 的 key
         */
        val INSTANCE_KEY: Attributes.Key<Instance> = Attributes.Key.create(Instance::class.java.name)

        /**
         *
         */
        val SOURCE_SERVICE_INFO: Attributes.Key<ServiceKey> = Attributes.Key.create(ServiceKey::class.java.name)

        /**
         * [io.grpc.Attributes] 中存放服务调用者的服务名称信息
         */
        val TARGET_SERVICE_KEY: Attributes.Key<String> = Attributes.Key.create("POLARIS_SOURCE_SERVICE")

        /**
         * [io.grpc.Attributes] 中存放服务调用者所在的命名空间信息
         */
        val TARGET_NAMESPACE_KEY: Attributes.Key<String> = Attributes.Key.create("POLARIS_SOURCE_NAMESPACE")
    }
}
