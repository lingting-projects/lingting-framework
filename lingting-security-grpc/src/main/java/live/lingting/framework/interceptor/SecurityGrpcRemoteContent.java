package live.lingting.framework.interceptor;

import live.lingting.framework.security.domain.SecurityToken;
import live.lingting.framework.thread.StackThreadLocal;

/**
 * @author lingting 2023-12-18 16:39
 */
public final class SecurityGrpcRemoteContent {

	static final StackThreadLocal<SecurityToken> THREAD_LOCAL = new StackThreadLocal<>();

	private SecurityGrpcRemoteContent() {throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");}

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
