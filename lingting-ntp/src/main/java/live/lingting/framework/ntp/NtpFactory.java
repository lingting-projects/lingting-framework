package live.lingting.framework.ntp;

import live.lingting.framework.thread.ThreadPool;
import live.lingting.framework.util.IpUtils;
import live.lingting.framework.util.ThreadUtils;
import live.lingting.framework.value.CycleValue;
import live.lingting.framework.value.StepValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * @author lingting 2023-12-27 15:47
 */
@Slf4j
@SuppressWarnings("java:S6548")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class NtpFactory {

	public static final String TIME_WINDOWS = "time.windows.com";

	public static final String TIME_NIST = "time.nist.gov";

	public static final String TIME_APPLE = "time.apple.com";

	public static final String TIME_ASIA = "time.asia.apple.com";

	public static final String CN_NTP = "cn.ntp.org.cn";

	public static final String NTP_NTSC = "ntp.ntsc.ac.cn";

	public static final String CN_POOL = "cn.pool.ntp.org";

	private static final String[] HOSTS = { TIME_WINDOWS, TIME_NIST, TIME_APPLE, TIME_ASIA, CN_NTP, NTP_NTSC,
			CN_POOL, };

	public static final StepValue<Long> STEP_INIT = StepValue.simple(1, null, 10L);

	public static final NtpFactory INSTANCE = new NtpFactory();

	public static Set<String> getDefaultHosts() {
		return Arrays.stream(HOSTS).collect(Collectors.toCollection(LinkedHashSet::new));
	}

	public static NtpFactory getDefault() {
		return INSTANCE;
	}

	private final Set<String> blockHosts = new HashSet<>();

	public Ntp create() throws InterruptedException {
		return create(HOSTS);
	}

	public Ntp create(String... hosts) throws InterruptedException {
		return create(Arrays.asList(hosts));
	}

	public Ntp create(Collection<String> hosts) throws InterruptedException {
		CycleValue<Long> cycle = CycleValue.step(STEP_INIT);
		for (String host : hosts) {
			if (blockHosts.contains(host)) {
				log.debug("[{}] is block host! skip", host);
				continue;
			}
			try {
				Ntp ntp = createByFuture(cycle, host);
				if (ntp != null) {
					return ntp;
				}
			}
			catch (UnknownHostException e) {
				log.warn("[{}] host cannot be resolved!", host);
				blockHosts.add(host);
			}
			catch (InterruptedException e) {
				throw e;
			}
			catch (TimeoutException e) {
				log.warn("Ntp initialization timeout! host: {}", host);
			}
			catch (Exception e) {
				log.error("Ntp initialization exception! host: {}", host);
			}
		}
		return null;
	}

	public Ntp createByFuture(CycleValue<Long> cycle, String host)
			throws UnknownHostException, ExecutionException, TimeoutException, InterruptedException {
		String ip = IpUtils.resolve(host);

		ThreadPool instance = ThreadUtils.instance();
		ThreadPoolExecutor executor = instance.getPool();
		CompletableFuture<Ntp> future = CompletableFuture.supplyAsync(() -> new Ntp(host, diff(host)), executor);
		try {
			Ntp ntp = future.get(cycle.next(), TimeUnit.SECONDS);
			if (ntp != null) {
				return ntp;
			}
		}
		finally {
			future.cancel(true);
		}

		future = CompletableFuture.supplyAsync(() -> new Ntp(ip, diff(ip)), executor);
		try {
			return future.get(cycle.next(), TimeUnit.SECONDS);
		}
		finally {
			future.cancel(true);
		}
	}

	public long diff(String host) {
		try (NTPUDPClient client = new NTPUDPClient()) {
			TimeInfo time = client.getTime(InetAddress.getByName(host));
			long system = System.currentTimeMillis();
			long ntp = time.getMessage().getTransmitTimeStamp().getTime();
			return ntp - system;
		}
		catch (Exception e) {
			throw new NtpException("Ntp get diff error! host: %s".formatted(host), e);
		}
	}

}
