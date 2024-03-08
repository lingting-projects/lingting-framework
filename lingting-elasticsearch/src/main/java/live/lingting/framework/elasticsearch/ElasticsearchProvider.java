package live.lingting.framework.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import live.lingting.framework.elasticsearch.datascope.ElasticsearchDataPermissionHandler;
import live.lingting.framework.jackson.JacksonUtils;
import lombok.experimental.UtilityClass;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.util.Arrays;
import java.util.function.Function;

/**
 * @author lingting 2024-03-06 20:00
 */
@UtilityClass
public class ElasticsearchProvider {

	public static RestClient restClient(String... hosts) {
		return restClient(Arrays.stream(hosts).map(HttpHost::create).toArray(HttpHost[]::new));
	}

	public static RestClient restClient(HttpHost... hosts) {
		return RestClient.builder(hosts).build();
	}

	public static JsonpMapper jacksonMapper() {
		return jacksonMapper(JacksonUtils.getMapper());
	}

	public static JsonpMapper jacksonMapper(ObjectMapper mapper) {
		return new JacksonJsonpMapper(mapper);
	}

	public static RestClientTransport transport(RestClient restClient) {
		return transport(restClient, jacksonMapper());
	}

	public static RestClientTransport transport(RestClient restClient, JsonpMapper jsonpMapper) {
		return new RestClientTransport(restClient, jsonpMapper);
	}

	public static ElasticsearchClient client(HttpHost... hosts) {
		return client(restClient(hosts));
	}

	public static ElasticsearchClient client(RestClient restClient) {
		return client(transport(restClient));
	}

	public static ElasticsearchClient client(ElasticsearchTransport transport) {
		return new ElasticsearchClient(transport);
	}

	public static <T> ElasticsearchApi<T> api(Class<T> cls, Function<T, String> idFunc,
			ElasticsearchProperties properties, ElasticsearchDataPermissionHandler handler,
			ElasticsearchClient client) {
		return new ElasticsearchApi<>(cls, idFunc, properties, handler, client);
	}

	public static <T> ElasticsearchApi<T> api(String index, Class<T> cls, Function<T, String> idFunc,
			ElasticsearchProperties properties, ElasticsearchDataPermissionHandler handler,
			ElasticsearchClient client) {
		return new ElasticsearchApi<>(index, cls, idFunc, properties, handler, client);
	}

}
