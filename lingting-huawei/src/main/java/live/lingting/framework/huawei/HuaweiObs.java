package live.lingting.framework.huawei;

import live.lingting.framework.http.HttpClient;
import live.lingting.framework.http.HttpResponse;
import live.lingting.framework.http.HttpUrlBuilder;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.huawei.exception.HuaweiObsException;
import live.lingting.framework.huawei.obs.HuaweiObsRequest;
import live.lingting.framework.huawei.properties.HuaweiObsProperties;
import live.lingting.framework.util.StringUtils;
import live.lingting.framework.value.multi.StringMultiValue;
import lombok.Setter;
import lombok.SneakyThrows;
import org.slf4j.Logger;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Collection;
import java.util.LinkedHashMap;

import static live.lingting.framework.huawei.HuaweiUtils.CLIENT;
import static live.lingting.framework.huawei.HuaweiUtils.HEADER_DATE;

/**
 * @author lingting 2024-09-13 13:45
 */
public abstract class HuaweiObs {

	public static final String HEADER_PREFIX = "x-obs";

	public static final String HEADER_ACL = "x-obs-acl";

	public static final String HEADER_TOKEN = "x-obs-security-token";

	protected final Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

	protected final HuaweiObsProperties properties;

	protected final String host;

	@Setter
	protected HttpClient client = CLIENT;

	protected HuaweiObs(HuaweiObsProperties properties) {
		this.properties = properties;
		this.host = "%s://%s.obs.%s.%s".formatted(properties.getScheme(), properties.getBucket(),
			properties.getRegion(), properties.getEndpoint());
	}

	protected void fillHeaders(HuaweiObsRequest request) {
		String date = HuaweiUtils.date();
		HttpHeaders headers = request.getHeaders();
		headers.put(HEADER_DATE, date);
		if (StringUtils.hasText(properties.getToken())) {
			headers.put(HEADER_TOKEN, properties.getToken());
		}
		if (request.getAcl() != null) {
			headers.put(HEADER_ACL, request.getAcl().getValue());
		}
	}

	protected void sign(HuaweiObsRequest request, HttpUrlBuilder urlBuilder) {
		HttpHeaders headers = request.getHeaders();

		String method = request.method().name().toUpperCase();
		String md5 = "";
		String type = request.contentType();
		String date = headers.first(HEADER_DATE);

		StringBuilder headersBuilder = new StringBuilder();
		headers.keys().stream().filter(k -> k.startsWith(HEADER_PREFIX)).sorted().forEach(k -> {
			Collection<String> vs = headers.get(k);
			if (vs.isEmpty()) {
				return;
			}
			headersBuilder.append(k).append(":").append(String.join(",", vs)).append("\n");
		});

		StringBuilder resourceBuilder = new StringBuilder();
		resourceBuilder.append("/").append(properties.getBucket()).append("/");
		String path = urlBuilder.buildPath();
		StringMultiValue params = urlBuilder.params();
		if (StringUtils.hasText(path) || !params.isEmpty()) {
			if (StringUtils.hasText(path)) {
				resourceBuilder.append(path, 1, path.length());
			}
			if (!params.isEmpty()) {
				resourceBuilder.append("?");
				LinkedHashMap<String, Collection<String>> map = new LinkedHashMap<>(params.size());
				params.keys().stream().sorted().forEach(k -> {
					Collection<String> collection = params.get(k);
					map.put(k, collection);
				});
				String query = HttpUrlBuilder.buildQuery(map);
				resourceBuilder.append(query);
			}
		}
		String source = method + "\n" + md5 + "\n" + type + "\n" + date + "\n" + headersBuilder + resourceBuilder;
		String authorization = HuaweiUtils.authorization(properties.getAk(), properties.getSk(), source);
		headers.authorization(authorization);
	}

	@SneakyThrows
	protected HttpResponse call(HuaweiObsRequest obsRequest) {
		fillHeaders(obsRequest);

		HttpUrlBuilder urlBuilder = obsRequest.urlBuilder().https().host(host);
		sign(obsRequest, urlBuilder);

		HttpRequest.Builder builder = obsRequest.builder();
		HttpHeaders headers = obsRequest.getHeaders();
		headers.each(builder::header);

		URI uri = urlBuilder.buildUri();
		HttpRequest request = builder.uri(uri).build();
		HttpResponse response = client.request(request);
		if (!response.is2xx()) {
			String body = response.string();
			log.error("HuaweiObs call error! uri: {}; code: {}; body:\n{}", uri, response.code(), body);
			throw new HuaweiObsException("request error! code: " + response.code());
		}
		return response;
	}

}
