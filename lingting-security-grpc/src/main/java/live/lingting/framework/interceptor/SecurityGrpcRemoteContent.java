package live.lingting.framework.interceptor;

import live.lingting.framework.security.domain.SecurityToken;
import live.lingting.framework.thread.StackThreadLocal;
import lombok.experimental.UtilityClass;

/**
 * @author lingting 2023-12-18 16:39
 */
@UtilityClass
public class SecurityGrpcRemoteContent {

	static final StackThreadLocal<SecurityToken> THREAD_LOCAL = new StackThreadLocal<>();

	public static SecurityToken get() {
		return THREAD_LOCAL.get();
	}

	public static void put(SecurityToken value) {
		THREAD_LOCAL.put(value);
	}

	public static void pop() {
		THREAD_LOCAL.pop();
	}

}
