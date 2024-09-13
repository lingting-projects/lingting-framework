package live.lingting.framework.huawei;

import live.lingting.framework.crypto.mac.Mac;
import live.lingting.framework.http.HttpClient;
import live.lingting.framework.time.DatePattern;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

/**
 * @author lingting 2024-09-13 11:54
 */
@UtilityClass
public class HuaweiUtils {

	public static final HttpClient CLIENT = HttpClient.okhttp()
		.disableSsl()
		.timeout(Duration.ofSeconds(5), Duration.ofSeconds(5))
		.build();

	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

	public static final Duration TOKEN_EARLY_EXPIRE = Duration.ofMinutes(15);

	public static final Duration CREDENTIAL_EXPIRE = Duration.ofDays(1);

	public static final Charset CHARSET = StandardCharsets.UTF_8;

	public static final String HEADER_DATE = "Date";

	public static LocalDateTime parse(String str, ZoneOffset zone) {
		LocalDateTime parse = LocalDateTime.parse(str, FORMATTER);
		return parse.plusSeconds(zone.getTotalSeconds());
	}

	public static String encode(String objectKey) {
		return URLEncoder.encode(objectKey, CHARSET);
	}

	/**
	 * <a href=
	 * "https://obs-community.obs.cn-north-1.myhuaweicloud.com/sign/header_signature.html">在线计算签名</a>
	 * </p>
	 * <a href=
	 * "https://support.huaweicloud.com/api-obs/obs_04_0010.html#obs_04_0010__section822416395814">签名携带方式</a>
	 */
	@SneakyThrows
	public static String authorization(String ak, String sk, String string) {
		Mac mac = Mac.hmacBuilder().sha1().secret(sk).charset(CHARSET).build();
		String base64 = mac.calculateBase64(string);
		return "OBS %s:%s".formatted(ak, base64);
	}

	public static String date() {
		return date(LocalDateTime.now());
	}

	public static String date(LocalDateTime now) {
		ZonedDateTime atZone = now.atZone(DatePattern.SYSTEM_ZONE_ID);
		ZonedDateTime atGmt = atZone.withZoneSameInstant(DatePattern.GZM_ZONE_ID);
		return RFC_1123_DATE_TIME.format(atGmt);
	}

}
