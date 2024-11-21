package live.lingting.framework.elasticsearch

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.json.JsonpMapper
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.ElasticsearchTransport
import co.elastic.clients.transport.rest_client.RestClientTransport
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.function.Function
import live.lingting.framework.elasticsearch.datascope.ElasticsearchDataPermissionHandler
import live.lingting.framework.jackson.JacksonUtils
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient

/**
 * @author lingting 2024-03-06 20:00
 */
object ElasticsearchProvider {
    @JvmStatic
    fun restClient(vararg hosts: String): RestClient {
        val map = hosts.mapNotNull() { HttpHost.create(it) }
        return restClient(*map.toTypedArray())
    }

    @JvmStatic
    fun restClient(vararg hosts: HttpHost): RestClient {
        return RestClient.builder(*hosts).build()
    }

    @JvmStatic
    @JvmOverloads
    fun jacksonMapper(mapper: ObjectMapper = JacksonUtils.mapper): JsonpMapper {
        return JacksonJsonpMapper(mapper)
    }

    @JvmStatic
    @JvmOverloads
    fun transport(restClient: RestClient, jsonpMapper: JsonpMapper = jacksonMapper()): RestClientTransport {
        return RestClientTransport(restClient, jsonpMapper)
    }

    @JvmStatic
    fun client(vararg hosts: HttpHost): ElasticsearchClient {
        return client(restClient(*hosts))
    }

    @JvmStatic
    fun client(restClient: RestClient): ElasticsearchClient {
        return client(transport(restClient))
    }

    @JvmStatic
    fun client(transport: ElasticsearchTransport): ElasticsearchClient {
        return ElasticsearchClient(transport)
    }

    @JvmStatic
    fun <T> api(
        cls: Class<T>, idFunc: Function<T, String>,
        properties: ElasticsearchProperties, handler: ElasticsearchDataPermissionHandler,
        client: ElasticsearchClient
    ): ElasticsearchApi<T> {
        return ElasticsearchApi(cls, idFunc, properties, handler, client)
    }

    @JvmStatic
    fun <T> api(
        index: String, cls: Class<T>, idFunc: Function<T, String>,
        properties: ElasticsearchProperties, handler: ElasticsearchDataPermissionHandler,
        client: ElasticsearchClient
    ): ElasticsearchApi<T> {
        return ElasticsearchApi(index, cls, idFunc, properties, handler, client)
    }

}
