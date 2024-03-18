
package live.lingting.polaris.grpc.server.impl;

import live.lingting.polaris.grpc.server.DelayRegister;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public final class WaitDelayRegister implements DelayRegister {

	private final Duration waitTime;

	public WaitDelayRegister(Duration waitTime) {
		this.waitTime = waitTime;
	}

	@Override
	public boolean allowRegis() {
		try {
			TimeUnit.SECONDS.sleep(waitTime.getSeconds());
		}
		catch (InterruptedException ignore) {
			Thread.currentThread().interrupt();
		}
		return true;
	}

}
