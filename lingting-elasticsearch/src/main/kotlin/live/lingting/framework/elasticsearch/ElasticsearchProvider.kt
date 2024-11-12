package live.lingting.framework.elasticsearch

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.json.JsonpMapper
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.ElasticsearchTransport
import co.elastic.clients.transport.rest_client.RestClientTransport
import com.fasterxml.jackson.databind.ObjectMapper
import live.lingting.framework.elasticsearch.datascope.ElasticsearchDataPermissionHandler
import live.lingting.framework.jackson.JacksonUtils
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import java.util.*
import java.util.function.Function

/**
 * @author lingting 2024-03-06 20:00
 */
class ElasticsearchProvider private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        @JvmStatic
        fun restClient(vararg hosts: String): RestClient {
            return restClient(*Arrays.stream<String>(hosts).map<HttpHost> { s: String? -> HttpHost.create(s) }.toArray<HttpHost> { _Dummy_.__Array__() })
        }

        @JvmStatic
        fun restClient(vararg hosts: HttpHost?): RestClient {
            return RestClient.builder(*hosts).build()
        }

        @JvmStatic
        @JvmOverloads
        fun jacksonMapper(mapper: ObjectMapper = JacksonUtils.getMapper()): JsonpMapper {
            return JacksonJsonpMapper(mapper)
        }

        @JvmStatic
        @JvmOverloads
        fun transport(restClient: RestClient?, jsonpMapper: JsonpMapper? = jacksonMapper()): RestClientTransport {
            return RestClientTransport(restClient, jsonpMapper)
        }

        @JvmStatic
        fun client(vararg hosts: HttpHost?): ElasticsearchClient {
            return client(restClient(*hosts))
        }

        @JvmStatic
        fun client(restClient: RestClient?): ElasticsearchClient {
            return client(transport(restClient))
        }

        @JvmStatic
        fun client(transport: ElasticsearchTransport?): ElasticsearchClient {
            return ElasticsearchClient(transport)
        }

        fun <T> api(
            cls: Class<T>, idFunc: Function<T, String?>,
            properties: ElasticsearchProperties, handler: ElasticsearchDataPermissionHandler?,
            client: ElasticsearchClient
        ): ElasticsearchApi<T> {
            return ElasticsearchApi(cls, idFunc, properties, handler, client)
        }

        fun <T> api(
            index: String, cls: Class<T>, idFunc: Function<T, String?>,
            properties: ElasticsearchProperties, handler: ElasticsearchDataPermissionHandler?,
            client: ElasticsearchClient
        ): ElasticsearchApi<T> {
            return ElasticsearchApi(index, cls, idFunc, properties, handler, client)
        }
    }
}
