package live.lingting.framework.ntp;

import live.lingting.framework.util.ThreadUtils;
import live.lingting.framework.value.WaitValue;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

/**
 * 中国 ntp 类
 *
 * @author lingting 2023/2/1 14:10
 */
@Slf4j
@UtilityClass
public class NtpCn {

	public static final ZoneOffset DEFAULT_ZONE_OFFSET = ZoneOffset.of("+8");

	public static final ZoneId DEFAULT_ZONE_ID = DEFAULT_ZONE_OFFSET.normalized();

	static final WaitValue<Ntp> instance = WaitValue.of();

	@SneakyThrows
	static void initNtpCN() {
		NtpFactory factory = NtpFactory.getDefault();
		Set<String> hosts = NtpFactory.getDefaultHosts();

		int index = 0;
		while (instance.isNull()) {
			index++;

			Ntp ntp = factory.create(hosts);
			if (ntp != null) {
				instance.update(ntp.zoneId(DEFAULT_ZONE_ID));
				return;
			}
			log.warn("Ntp create failed, retry count: {}", index);
		}
	}

	@SneakyThrows
	public static Ntp instance() {
		if (!instance.isNull()) {
			return instance.getValue();
		}

		return instance.compute(new UnaryOperator<Ntp>() {
			@Override
			@SneakyThrows
			public Ntp apply(Ntp v) {
				if (v != null) {
					return v;
				}

				ThreadUtils.execute("NTP-INIT", NtpCn::initNtpCN);
				return instance.notNull();
			}
		});
	}

	public long diff() {
		return instance().diff();
	}

	public long currentMillis() {
		return instance().currentMillis();
	}

	public Instant instant() {
		return instance().instant();
	}

	public LocalDateTime now() {
		return instance().now();
	}

	public long plusSeconds(long seconds) {
		return plusMillis(seconds * 1000);
	}

	public long plusMillis(long millis) {
		return instance().plusMillis(millis);
	}

	public long plus(long time, TimeUnit unit) {
		return plusMillis(unit.toMillis(time));
	}

}
