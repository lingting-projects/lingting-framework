package live.lingting.polaris.grpc.metadata

import io.grpc.Context

import java.util.concurrent.ConcurrentHashMap

/**
 * copy from
 * https://github.com/Tencent/spring-cloud-tencent/blob/main/spring-cloud-tencent-commons/src/main/java/com/tencent/cloud/common/metadata/MetadataContext.java
 *
 * @author [liaochuntao](mailto:liaochuntao@live.com)
 */
class MetadataContext {
    private var fragmentContexts: MutableMap<String, MutableMap<String, String?>>

    init {
        this.fragmentContexts = ConcurrentHashMap()
    }

    val headerFragment: Map<String, String?>
        get() = getFragment(FRAGMENT_HEADER)

    val grpcContextFragment: Map<String, String?>
        get() = getFragment(FRAGMENT_GRPC_CONTEXT)

    private fun getFragment(fragment: String): Map<String, String?> {
        val fragmentContext = fragmentContexts[fragment] ?: return emptyMap<String, String>()
        return Collections.unmodifiableMap(fragmentContext)
    }

    fun putHeaderFragment(key: String, value: String?) {
        putHeaderFragment(FRAGMENT_HEADER, key, value)
    }

    fun putContextFragment(key: String, value: String?) {
        putHeaderFragment(FRAGMENT_GRPC_CONTEXT, key, value)
    }

    private fun putHeaderFragment(fragment: String, key: String, value: String?) {
        val fragmentContext = fragmentContexts.computeIfAbsent(
            fragment
        ) { k: String? -> ConcurrentHashMap() }
        fragmentContext[key] = value
    }

    fun reset() {
        fragmentContexts = ConcurrentHashMap()
    }

    override fun toString(): String {
        return "MetadataContext{fragmentContexts=$fragmentContexts}"
    }

    companion object {
        val METADATA_CONTEXT_KEY: Context.Key<MetadataContext> = Context.keyWithDefault(
            "MetadataContext",
            MetadataContext()
        )

        const val FRAGMENT_HEADER: String = "header"

        const val FRAGMENT_GRPC_CONTEXT: String = "grpc_context"
    }
}
