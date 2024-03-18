
package live.lingting.polaris.grpc.server.impl;

import live.lingting.polaris.grpc.server.DelayRegister;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class NoopDelayRegister implements DelayRegister {

	@Override
	public boolean allowRegis() {
		return true;
	}

}
