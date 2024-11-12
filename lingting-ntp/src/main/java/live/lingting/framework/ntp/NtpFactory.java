package live.lingting.framework.ntp;

import live.lingting.framework.util.IpUtils;
import live.lingting.framework.util.ThreadUtils;
import live.lingting.framework.value.CycleValue;
import live.lingting.framework.value.StepValue;
import live.lingting.framework.value.cycle.StepCycleValue;
import live.lingting.framework.value.step.LongStepValue;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.slf4j.Logger;

import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * @author lingting 2023-12-27 15:47
 */
@SuppressWarnings("java:S6548")
public class NtpFactory {

	public static final StepValue<Long> STEP_INIT = new LongStepValue(1, null, Long.valueOf(10));

	public static final NtpFactory INSTANCE = new NtpFactory();

	private static final String[] HOSTS = {"time.windows.com", "time.nist.gov", "time.apple.com",
		"time.asia.apple.com", "cn.ntp.org.cn", "ntp.ntsc.ac.cn", "cn.pool.ntp.org", "ntp.aliyun.com",
		"ntp1.aliyun.com", "ntp2.aliyun.com", "ntp3.aliyun.com", "ntp4.aliyun.com", "ntp5.aliyun.com",
		"ntp6.aliyun.com", "ntp7.aliyun.com",};
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(NtpFactory.class);

	private final Set<String> blockHosts = new HashSet<>();

	protected NtpFactory() {}

	public static Set<String> getDefaultHosts() {
		return Arrays.stream(HOSTS).collect(Collectors.toCollection(LinkedHashSet::new));
	}

	public static NtpFactory getDefault() {
		return INSTANCE;
	}

	public Ntp create() throws InterruptedException {
		return create(HOSTS);
	}

	public Ntp create(String... hosts) throws InterruptedException {
		return create(Arrays.asList(hosts));
	}

	public Ntp create(Collection<String> hosts) throws InterruptedException {
		CycleValue<Long> cycle = new StepCycleValue<>(STEP_INIT);
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

		CompletableFuture<Ntp> future = ThreadUtils.async(() -> {
			long diff = diff(host);
			return new Ntp(host, diff);
		});
		try {
			Long next = cycle.next();
			Ntp ntp = future.get(next, TimeUnit.SECONDS);
			if (ntp != null) {
				return ntp;
			}
		}
		finally {
			future.cancel(true);
		}

		future = ThreadUtils.async(() -> new Ntp(ip, diff(ip)));
		try {
			Long next = cycle.next();
			return future.get(next, TimeUnit.SECONDS);
		}
		finally {
			future.cancel(true);
		}
	}

	public long diff(String host) {
		try (NTPUDPClient client = new NTPUDPClient()) {
			client.setDefaultTimeout(Duration.ofSeconds(5));
			client.open();
			client.setSoTimeout(Duration.ofSeconds(3));
			TimeInfo time = client.getTime(InetAddress.getByName(host));
			long system = System.currentTimeMillis();
			long ntp = time.getMessage().getTransmitTimeStamp().getTime();
			return ntp - system;
		}
		catch (SocketTimeoutException e) {
			throw new NtpException("Ntp get diff timeout! host: %s".formatted(host), e);
		}
		catch (Exception e) {
			throw new NtpException("Ntp get diff error! host: %s".formatted(host), e);
		}
	}

}
