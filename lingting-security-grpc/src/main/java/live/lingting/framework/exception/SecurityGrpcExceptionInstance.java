package live.lingting.framework.exception;

import io.grpc.MethodDescriptor;
import io.grpc.Status;
import live.lingting.framework.grpc.exception.GrpcExceptionHandler;
import live.lingting.framework.grpc.exception.GrpcExceptionInstance;
import live.lingting.framework.security.exception.AuthorizationException;
import live.lingting.framework.security.exception.PermissionsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lingting 2023-12-15 17:15
 */
@Slf4j
@RequiredArgsConstructor
public class SecurityGrpcExceptionInstance implements GrpcExceptionInstance {

	/**
	 * 鉴权异常
	 */
	@GrpcExceptionHandler(AuthorizationException.class)
	public Status handlerAuthorizationException(MethodDescriptor<?, ?> descriptor, AuthorizationException e) {
		log.error("Authorization error! target: {}. {}", descriptor.getFullMethodName(), e.getMessage());
		return Status.UNAUTHENTICATED.withCause(e).withDescription(e.getMessage());
	}

	/**
	 * 权限异常
	 */
	@GrpcExceptionHandler(PermissionsException.class)
	public Status handlerPermissionsException(MethodDescriptor<?, ?> descriptor, PermissionsException e) {
		log.error("Permissions error! target: {}. {}", descriptor.getFullMethodName(), e.getMessage());
		return Status.PERMISSION_DENIED.withCause(e).withDescription(e.getMessage());
	}

}
