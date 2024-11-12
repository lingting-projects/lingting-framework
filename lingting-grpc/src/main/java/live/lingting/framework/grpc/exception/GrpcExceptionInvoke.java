package live.lingting.framework.grpc.exception;

import io.grpc.Attributes;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.SecurityLevel;
import io.grpc.ServerCall;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author lingting 2024-03-27 09:40
 */
public class GrpcExceptionInvoke {

	private final GrpcExceptionInstance instance;

	private final Method method;

	private final GrpcExceptionHandler handler;

	public GrpcExceptionInvoke(GrpcExceptionInstance instance, Method method, GrpcExceptionHandler handler) {
		this.instance = instance;
		this.method = method;
		this.handler = handler;
	}

	protected Object[] args(Exception e, ServerCall<?, ?> call, Metadata metadata) {
		int count = method.getParameterCount();
		if (count < 1) {
			return new Object[0];
		}
		Object[] args = new Object[count];
		Parameter[] parameters = method.getParameters();

		for (int i = 0; i < count; i++) {
			Parameter parameter = parameters[i];
			Class<?> type = parameter.getType();
			if (type.isAssignableFrom(e.getClass())) {
				args[i] = e;
			}
			else if (type.isAssignableFrom(Metadata.class)) {
				args[i] = metadata;
			}
			else if (type.isAssignableFrom(Attributes.class)) {
				args[i] = call.getAttributes();
			}
			else if (type.isAssignableFrom(SecurityLevel.class)) {
				args[i] = call.getSecurityLevel();
			}
			else if (type.isAssignableFrom(MethodDescriptor.class)) {
				args[i] = call.getMethodDescriptor();
			}
			else if (type.isAssignableFrom(ServerCall.class)) {
				args[i] = call;
			}
		}

		return args;
	}

	public boolean isSupport(Class<?> cls) {
		for (Class<? extends Throwable> a : handler.value()) {
			if (a.isAssignableFrom(cls)) {
				return true;
			}
		}
		return false;
	}


	public Object invoke(Exception e, ServerCall<?, ?> call, Metadata metadata) {
		Object[] args = args(e, call, metadata);
		return method.invoke(instance, args);
	}

}
