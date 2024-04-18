package live.lingting.framework.exception;

import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import live.lingting.framework.security.exception.AuthorizationException;
import live.lingting.framework.security.exception.PermissionsException;
import lombok.experimental.UtilityClass;

import java.util.function.Consumer;

/**
 * @author lingting 2024-04-18 16:24
 */
@UtilityClass
public class SecurityGrpcThrowing {

	public static Exception convert(Exception e) {
		if (e instanceof StatusException || e instanceof StatusRuntimeException) {
			Status status = e instanceof StatusException se ? se.getStatus() : ((StatusRuntimeException) e).getStatus();
			switch (status.getCode()) {
				case UNAUTHENTICATED:
					return new AuthorizationException(status.getDescription(), e);
				case PERMISSION_DENIED:
					return new PermissionsException(status.getDescription(), e);
				default:
					break;
			}
		}
		return e;
	}

	public static void throwing(Exception ex, Consumer<Exception> consumer)
			throws AuthorizationException, PermissionsException {
		Exception e = convert(ex);
		if (e instanceof AuthorizationException ae) {
			throw ae;
		}
		if (e instanceof PermissionsException pe) {
			throw pe;
		}

		consumer.accept(e);
	}

}
