package live.lingting.framework.huawei;

import live.lingting.framework.http.HttpResponse;
import live.lingting.framework.http.HttpUrlBuilder;
import live.lingting.framework.http.api.ApiClient;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.huawei.exception.HuaweiObsException;
import live.lingting.framework.huawei.obs.HuaweiObsRequest;
import live.lingting.framework.huawei.properties.HuaweiObsProperties;
import live.lingting.framework.util.StringUtils;

import java.util.Collection;

import static live.lingting.framework.huawei.HuaweiUtils.HEADER_DATE;

/**
 * @author lingting 2024-09-13 13:45
 */
public abstract class HuaweiObs extends ApiClient<HuaweiObsRequest> {

	public static final String HEADER_PREFIX = "x-obs";

	public static final String HEADER_ACL = "x-obs-acl";

	public static final String HEADER_TOKEN = "x-obs-security-token";

	protected final HuaweiObsProperties properties;

	protected HuaweiObs(HuaweiObsProperties properties) {
		super("%s://%s.obs.%s.%s".formatted(properties.getScheme(), properties.getBucket(), properties.getRegion(),
				properties.getEndpoint()));
		this.properties = properties;
	}

	@Override
	protected void configure(HuaweiObsRequest request, HttpHeaders headers) {
		String date = HuaweiUtils.date();
		headers.put(HEADER_DATE, date);
		if (StringUtils.hasText(properties.getToken())) {
			headers.put(HEADER_TOKEN, properties.getToken());
		}

		if (request.getAcl() != null) {
			headers.put(HEADER_ACL, request.getAcl().getValue());
		}
	}

	@Override
	protected void configure(HuaweiObsRequest request, HttpHeaders headers, HttpUrlBuilder urlBuilder) {
		String authorization = authorization(request, headers, urlBuilder.buildPath(), urlBuilder.buildQuery());
		headers.authorization(authorization);
	}

	@Override
	protected HttpResponse checkout(HuaweiObsRequest request, HttpResponse response) {
		if (!response.is2xx()) {
			String string = response.string();
			log.error("HuaweiObs call error! uri: {}; code: {}; body:\n{}", response.uri(), response.code(), string);
			throw new HuaweiObsException("request error! code: " + response.code());
		}
		return response;
	}

	protected String authorization(HuaweiObsRequest request, HttpHeaders headers, String path, String query) {
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
		if (StringUtils.hasText(path)) {
			resourceBuilder.append(path, 1, path.length());
		}

		if (StringUtils.hasText(query)) {
			resourceBuilder.append("?").append(query);
		}
		String source = method + "\n" + md5 + "\n" + type + "\n" + date + "\n" + headersBuilder + resourceBuilder;
		return HuaweiUtils.authorization(properties.getAk(), properties.getSk(), source);
	}

}
