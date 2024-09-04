package live.lingting.framework.dingtalk;

import live.lingting.framework.crypto.mac.Mac;
import live.lingting.framework.dingtalk.message.DingTalkMessage;
import live.lingting.framework.http.HttpClient;
import live.lingting.framework.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.function.Supplier;

/**
 * 订单消息发送
 *
 * @author lingting 2020/6/10 21:25
 */
@Getter
@Accessors(chain = true)
public class DingTalkSender {

	protected static final HttpClient CLIENT = HttpClient.okhttp()
		.timeout(Duration.ofSeconds(10), Duration.ofSeconds(10))
		.build();

	/**
	 * 请求路径
	 */
	protected final String url;

	/**
	 * 密钥
	 */
	protected String secret;

	protected Mac mac;

	@Setter
	private Supplier<Long> currentTimeMillisSupplier = System::currentTimeMillis;

	public DingTalkSender(String url) {
		this.url = url;
	}

	/**
	 * 发送消息 根据参数值判断使用哪种发送方式
	 */
	public DingTalkResponse sendMessage(DingTalkMessage message) {
		if (StringUtils.hasText(getSecret())) {
			return sendSecretMessage(message);
		}
		else {
			return sendNormalMessage(message);
		}
	}

	/**
	 * 未使用 加签 安全设置 直接发送
	 */
	public DingTalkResponse sendNormalMessage(DingTalkMessage message) {
		return request(message, false);
	}

	/**
	 * 使用 加签 安全设置 发送
	 */
	public DingTalkResponse sendSecretMessage(DingTalkMessage message) {
		return request(message, true);
	}

	/**
	 * 设置密钥
	 */
	public DingTalkSender setSecret(String secret) {
		if (StringUtils.hasText(secret)) {
			this.secret = secret;
			this.mac = Mac.hmacBuilder().sha256().secret(secret).build();
		}
		else {
			this.secret = null;
			this.mac = null;
		}
		return this;
	}

	/**
	 * 获取签名后的请求路径
	 * @param timestamp 当前时间戳
	 */
	@SneakyThrows
	public String secret(long timestamp) {
		String source = timestamp + "\n" + secret;
		String base64 = mac.calculateBase64(source);
		String sign = URLEncoder.encode(base64, StandardCharsets.UTF_8);
		return "%s&timestamp=%d&sign=%s".formatted(url, timestamp, sign);
	}

	/**
	 * 发起消息请求
	 * @param dingTalkMessage 消息内容
	 * @param isSecret 是否签名 true 签名
	 * @return java.lang.String
	 */
	@SneakyThrows
	public DingTalkResponse request(DingTalkMessage dingTalkMessage, boolean isSecret) {
		String message = dingTalkMessage.generate();
		Long timestamp = getCurrentTimeMillisSupplier().get();

		String requestUrl = isSecret ? secret(timestamp) : getUrl();

		HttpRequest request = HttpRequest.newBuilder()
			.POST(HttpRequest.BodyPublishers.ofString(message, StandardCharsets.UTF_8))
			.uri(URI.create(requestUrl))
			.header("Content-Type", "application/json")
			.build();

		String json = CLIENT.request(request, String.class);
		return DingTalkResponse.of(json);
	}

}
