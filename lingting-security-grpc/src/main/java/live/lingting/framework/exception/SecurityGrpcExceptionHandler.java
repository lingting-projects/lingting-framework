package live.lingting.framework.exception;

import io.grpc.MethodDescriptor;
import io.grpc.Status;
import live.lingting.framework.security.exception.AuthorizationException;
import live.lingting.framework.security.exception.PermissionsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lingting 2023-12-15 17:15
 */
@Slf4j
@RequiredArgsConstructor
public class SecurityGrpcExceptionHandler {

	/**
	 * 鉴权异常
	 */
	public Status handlerAuthorizationException(MethodDescriptor<?, ?> descriptor, AuthorizationException e) {
		log.error("Authorization error! {}. {}", descriptor.getFullMethodName(), e.getMessage());
		return Status.UNAUTHENTICATED.withCause(e);
	}

	/**
	 * 权限异常
	 */
	public Status handlerPermissionsException(MethodDescriptor<?, ?> descriptor, PermissionsException e) {
		log.error("Permissions error! {}. {}", descriptor.getFullMethodName(), e.getMessage());
		return Status.PERMISSION_DENIED.withCause(e);
	}

	/**
	 * 其他异常
	 */
	public Status handlerOther(MethodDescriptor<?, ?> descriptor, Exception e) {
		log.error("Authorize valid error! {}.", descriptor.getFullMethodName(), e);
		return Status.ABORTED.withCause(e);
	}

}
