package live.lingting.framework.elasticsearch

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.json.JsonpMapper
import co.elastic.clients.transport.ElasticsearchTransport
import live.lingting.framework.elasticsearch.ElasticsearchProvider.client
import live.lingting.framework.elasticsearch.ElasticsearchProvider.jacksonMapper
import live.lingting.framework.elasticsearch.ElasticsearchProvider.transport
import live.lingting.framework.elasticsearch.annotation.Index
import live.lingting.framework.elasticsearch.composer.QueryComposer
import live.lingting.framework.elasticsearch.datascope.DataScopeInterceptor
import live.lingting.framework.elasticsearch.datascope.ElasticsearchDataScope
import live.lingting.framework.elasticsearch.polymerize.PolymerizeFactory
import org.elasticsearch.client.RestClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-03-08 10:17
 */
internal class ElasticsearchApiTest {
    val host: String = "http://192.168.91.129:9200"

    var restClient: RestClient? = null

    var jsonpMapper: JsonpMapper? = null

    var transport: ElasticsearchTransport? = null

    var client: ElasticsearchClient? = null

    var api: ElasticsearchApi<Entity>? = null

    var allowDefault: Boolean = false

    @BeforeEach
    fun before() {
        restClient = ElasticsearchProvider.restClient(host)
        jsonpMapper = jacksonMapper()
        transport = transport(restClient!!, jsonpMapper!!)
        client = client(transport!!)

        val scope = object : ElasticsearchDataScope {
            override val resource: String = "null"
            override fun includes(p: String): Boolean = true

            override fun handler(p: IndexInfo): Query? = if (allowDefault) QueryComposer.term("space.name", "default") else null
        }
        val scopes = listOf(scope)
        val interceptors = listOf(DataScopeInterceptor(scopes))

        api = ElasticsearchApi(Entity::class.java, PolymerizeFactory(), { it.id!! }, ElasticsearchProperties(), interceptors, client!!)
    }

    @Test
    fun test() {
        val list = api!!.list()
        assertFalse(list.isEmpty())
        allowDefault = true
        val byQuery = api!!.getByQuery()
        assertNotNull(byQuery)
        assertEquals("Default", byQuery!!.space!!["name"])
    }

    @Index(index = ".kibana_8.12.2_001")
    class Entity {
        var id: String? = null

        var space: Map<String, Any>? = null

        var config: Map<String, Any>? = null

        var references: Any? = null
    }
}
