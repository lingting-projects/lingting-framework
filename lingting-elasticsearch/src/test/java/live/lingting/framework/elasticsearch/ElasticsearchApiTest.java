package live.lingting.framework.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import live.lingting.framework.elasticsearch.composer.QueryComposer;
import live.lingting.framework.elasticsearch.datascope.DefaultElasticsearchDataPermissionHandler;
import live.lingting.framework.elasticsearch.datascope.ElasticsearchDataScope;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static live.lingting.framework.elasticsearch.ElasticsearchProvider.api;
import static live.lingting.framework.elasticsearch.ElasticsearchProvider.client;
import static live.lingting.framework.elasticsearch.ElasticsearchProvider.jacksonMapper;
import static live.lingting.framework.elasticsearch.ElasticsearchProvider.restClient;
import static live.lingting.framework.elasticsearch.ElasticsearchProvider.transport;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author lingting 2024-03-08 10:17
 */
class ElasticsearchApiTest {

	final String host = "http://192.168.91.129:9200";

	RestClient restClient;

	JsonpMapper jsonpMapper;

	ElasticsearchTransport transport;

	ElasticsearchClient client;

	ElasticsearchApi<Entity> api;

	boolean allowDefault = false;

	@BeforeEach
	void before() {
		restClient = restClient(host);
		jsonpMapper = jacksonMapper();
		transport = transport(restClient, jsonpMapper);
		client = client(transport);

		ElasticsearchDataScope scope = new ElasticsearchDataScope() {
			@Override
			public String getResource() {
				return "null";
			}

			@Override
			public boolean includes(String index) {
				return true;
			}

			@Override
			public Query invoke(String index) {
				return QueryComposer.term("space.name", "default");
			}
		};

		DefaultElasticsearchDataPermissionHandler handler = new DefaultElasticsearchDataPermissionHandler(
				Collections.singletonList(scope)) {
			@Override
			public boolean ignorePermissionControl(String index) {
				return !allowDefault;
			}
		};
		api = api(".kibana_8.12.2_001", Entity.class, Entity::getId, new ElasticsearchProperties(), handler, client);
	}

	@SneakyThrows
	@Test
	void test() {
		List<Entity> list = api.list();
		assertFalse(list.isEmpty());
		allowDefault = true;
		Entity byQuery = api.getByQuery();
		assertNotNull(byQuery);
		assertEquals("Default", byQuery.space.get("name"));
	}

	@Getter
	@Setter
	static class Entity {

		private String id;

		private Map<String, Object> space;

		private Map<String, Object> config;

		private Object references;

	}

}
