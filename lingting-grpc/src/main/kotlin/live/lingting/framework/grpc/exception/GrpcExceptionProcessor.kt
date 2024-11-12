package live.lingting.framework.grpc.exception;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.Status;
import live.lingting.framework.Sequence;
import live.lingting.framework.util.AnnotationUtils;
import live.lingting.framework.util.ArrayUtils;
import live.lingting.framework.util.ClassUtils;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lingting 2024-03-27 09:39
 */
public class GrpcExceptionProcessor {

	private static final GrpcExceptionInvoke DEFAULT = new GrpcExceptionThrowInvoke();

	private static final Map<Class<?>, GrpcExceptionInvoke> CACHE = new ConcurrentHashMap<>();
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(GrpcExceptionProcessor.class);

	private final List<GrpcExceptionInstance> instances;

	private final List<GrpcExceptionInvoke> invokes = new ArrayList<>();

	public GrpcExceptionProcessor(Collection<GrpcExceptionInstance> instances) {
		this.instances = Sequence.asc(instances);
		init();
	}

	public void init() {
		for (GrpcExceptionInstance instance : instances) {
			for (Method method : ClassUtils.methods(instance.getClass())) {
				GrpcExceptionHandler handler = AnnotationUtils.findAnnotation(method, GrpcExceptionHandler.class);
				if (handler != null && !ArrayUtils.isEmpty(handler.value())) {
					invokes.add(new GrpcExceptionInvoke(instance, method, handler));
				}
			}
		}
		CACHE.clear();
	}

	public GrpcExceptionInvoke find(Exception e) {
		return CACHE.computeIfAbsent(e.getClass(), k -> {
			for (GrpcExceptionInvoke invoke : invokes) {
				if (invoke.isSupport(k)) {
					return invoke;
				}
			}
			return DEFAULT;
		});
	}

	public static class GrpcExceptionThrowInvoke extends GrpcExceptionInvoke {

		public GrpcExceptionThrowInvoke() {
			super(null, null, null);
		}

		@Override
		public boolean isSupport(Class<?> cls) {
			return true;
		}


		@Override
		public Object invoke(Exception e, ServerCall<?, ?> call, Metadata metadata) {
			log.error("unknown exception. target: {}", call.getMethodDescriptor().getFullMethodName(), e);
			return Status.ABORTED.withCause(e);
		}

	}

}
