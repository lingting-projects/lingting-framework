
package live.lingting.polaris.grpc.server;

import com.tencent.polaris.client.api.SDKContext;
import io.grpc.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public final class GraceOffline {

	private static final Logger LOGGER = LoggerFactory.getLogger(GraceOffline.class);

	private final Server grpcServer;

	private final Duration maxWaitDuration;

	private final SDKContext context;

	private final AtomicBoolean executed = new AtomicBoolean(false);

	public GraceOffline(Server server, Duration maxWaitDuration, SDKContext context) {
		this.grpcServer = server;
		this.context = context;
		this.maxWaitDuration = maxWaitDuration;
	}

	public Server shutdown() {
		if (!executed.compareAndSet(false, true)) {
			return grpcServer;
		}
		LOGGER.info("[grpc-polaris] begin grace shutdown");
		grpcServer.shutdown();

		try {
			// 等待 4 个 pull 时间间隔
			TimeUnit.SECONDS.sleep(4 * 2);
		}
		catch (InterruptedException ignore) {
			Thread.currentThread().interrupt();
		}

		try {
			grpcServer.awaitTermination(maxWaitDuration.toMillis(), TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException ignore) {
			Thread.currentThread().interrupt();
		}
		context.close();
		return grpcServer;
	}

}
