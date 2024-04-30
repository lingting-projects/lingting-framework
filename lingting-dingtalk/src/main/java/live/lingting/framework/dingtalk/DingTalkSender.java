package live.lingting.framework.dingtalk;

import live.lingting.framework.dingtalk.message.DingTalkMessage;
import live.lingting.framework.okhttp.OkHttp3;
import live.lingting.framework.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
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

	public static final MediaType MEDIA = MediaType.parse("application/json");

	protected static final OkHttp3 CLIENT = OkHttp3.builder()
		.timeout(Duration.ofSeconds(10), Duration.ofSeconds(10))
		.build();

	/**
	 * 请求路径
	 */
	private final String url;

	/**
	 * 密钥
	 */
	private String secret;

	@Setter
	private Supplier<Long> currentTimeMillisSupplier = System::currentTimeMillis;

	public DingTalkSender(String url) {
		this.url = url;
	}

	/**
	 * 发送消息 根据参数值判断使用哪种发送方式
	 *
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
		this.secret = StringUtils.hasText(secret) ? secret : null;
		return this;
	}

	/**
	 * 获取签名后的请求路径
	 * @param timestamp 当前时间戳
	 */
	@SneakyThrows
	public String secret(long timestamp) {
		SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(key);

		byte[] secretBytes = (timestamp + "\n" + secret).getBytes(StandardCharsets.UTF_8);
		byte[] bytes = mac.doFinal(secretBytes);

		String base64 = java.util.Base64.getEncoder().encodeToString(bytes);
		String sign = URLEncoder.encode(base64, StandardCharsets.UTF_8);
		return "%s&timestamp=%d&sign=%s".formatted(url, timestamp, sign);
	}

	/**
	 * 发起消息请求
	 * @param dingTalkMessage 消息内容
	 * @param isSecret 是否签名 true 签名
	 * @return java.lang.String
	 */
	public DingTalkResponse request(DingTalkMessage dingTalkMessage, boolean isSecret) {
		String message = dingTalkMessage.generate();
		Long timestamp = getCurrentTimeMillisSupplier().get();

		String requestUrl = isSecret ? secret(timestamp) : getUrl();
		RequestBody requestBody = RequestBody.create(message, MEDIA);

		String response = CLIENT.post(requestUrl, requestBody, String.class);
		return DingTalkResponse.of(response);
	}

}
