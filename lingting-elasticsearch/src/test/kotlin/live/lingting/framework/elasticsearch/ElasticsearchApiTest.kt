package live.lingting.framework.elasticsearch

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.json.JsonpMapper
import co.elastic.clients.transport.ElasticsearchTransport
import live.lingting.framework.elasticsearch.ElasticsearchProvider.api
import live.lingting.framework.elasticsearch.ElasticsearchProvider.client
import live.lingting.framework.elasticsearch.ElasticsearchProvider.jacksonMapper
import live.lingting.framework.elasticsearch.ElasticsearchProvider.transport
import live.lingting.framework.elasticsearch.composer.QueryComposer
import live.lingting.framework.elasticsearch.datascope.DefaultElasticsearchDataPermissionHandler
import live.lingting.framework.elasticsearch.datascope.ElasticsearchDataScope
import org.elasticsearch.client.RestClient
import org.junit.jupiter.api.Assertions
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

        val scope: ElasticsearchDataScope = object : ElasticsearchDataScope {
            override val resource: String
                get() = "null"

            override fun includes(index: String): Boolean {
                return true
            }

            override fun invoke(index: String): Query {
                return QueryComposer.term("space.name", "default")
            }
        }

        val handler: DefaultElasticsearchDataPermissionHandler = object : DefaultElasticsearchDataPermissionHandler(
            listOf(scope)
        ) {
            override fun ignorePermissionControl(index: String): Boolean {
                return !allowDefault
            }
        }
        api = api(".kibana_8.12.2_001", Entity::class.java, { obj -> obj.id!! }, ElasticsearchProperties(), handler, client!!)
    }


    @Test
    fun test() {
        val list = api!!.list()
        Assertions.assertFalse(list.isEmpty())
        allowDefault = true
        val byQuery = api!!.getByQuery()
        Assertions.assertNotNull(byQuery)
        Assertions.assertEquals("Default", byQuery!!.space!!["name"])
    }

    internal class Entity {
        var id: String? = null

        var space: Map<String, Any>? = null

        var config: Map<String, Any>? = null

        var references: Any? = null
    }
}
