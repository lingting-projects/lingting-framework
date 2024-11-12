package live.lingting.framework.grpc;

import io.grpc.BindableService;
import io.grpc.MethodDescriptor;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptor;
import io.grpc.ServerMethodDefinition;
import io.grpc.ServerServiceDefinition;
import io.grpc.ServiceDescriptor;
import live.lingting.framework.Sequence;
import live.lingting.framework.context.ContextComponent;
import live.lingting.framework.grpc.interceptor.AbstractServerInterceptor;
import live.lingting.framework.util.ClassUtils;
import live.lingting.framework.util.ThreadUtils;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

/**
 * @author lingting 2023-04-14 17:38
 */
public class GrpcServer implements ContextComponent {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(GrpcServer.class);
	private final Server server;

	private final Map<String, Class<? extends BindableService>> serviceNameMap;

	private final Map<String, Method> fullMethodNameMap;

	public GrpcServer(ServerBuilder<?> builder, Collection<ServerInterceptor> interceptors,
					  Collection<BindableService> services) {
		// 升序排序
		List<ServerInterceptor> asc = Sequence.asc(interceptors);
		// 获取一个游标在尾部的迭代器
		ListIterator<ServerInterceptor> iterator = asc.listIterator(asc.size());
		// 服务端是最后注册的拦截器最先执行, 所以要倒序注册
		while (iterator.hasPrevious()) {
			ServerInterceptor previous = iterator.previous();
			if (previous instanceof AbstractServerInterceptor interceptor) {
				interceptor.setServer(this);
			}
			builder.intercept(previous);
		}

		this.serviceNameMap = new HashMap<>();
		this.fullMethodNameMap = new HashMap<>();

		// 注册服务
		for (BindableService service : services) {
			builder.addService(service);
			fillMap(service);
		}

		this.server = builder.build();
	}

	/**
	 * 填充服务名map和全方法名map
	 */
	protected void fillMap(BindableService service) {
		Class<? extends BindableService> cls = service.getClass();

		ServerServiceDefinition serverServiceDefinition = service.bindService();
		ServiceDescriptor serviceDescriptor = serverServiceDefinition.getServiceDescriptor();

		serviceNameMap.put(serviceDescriptor.getName(), cls);

		for (ServerMethodDefinition<?, ?> serverMethodDefinition : serverServiceDefinition.getMethods()) {
			MethodDescriptor<?, ?> methodDescriptor = serverMethodDefinition.getMethodDescriptor();
			String fullMethodName = methodDescriptor.getFullMethodName();
			fullMethodNameMap.put(fullMethodName, resolve(methodDescriptor, cls));
		}
	}

	public boolean isRunning() {
		return !server.isShutdown() && !server.isTerminated();
	}

	public int port() {
		return server.getPort();
	}

	public Class<? extends BindableService> findClass(ServiceDescriptor descriptor) {
		return serviceNameMap.get(descriptor.getName());
	}

	@SuppressWarnings("unchecked")
	public Class<? extends BindableService> findClass(MethodDescriptor<?, ?> descriptor) {
		Method method = findMethod(descriptor);
		return (Class<? extends BindableService>) method.getDeclaringClass();
	}

	public Method findMethod(MethodDescriptor<?, ?> descriptor) {
		return fullMethodNameMap.get(descriptor.getFullMethodName());
	}

	protected Method resolve(MethodDescriptor<?, ?> descriptor, Class<? extends BindableService> cls) {
		String bareMethodName = descriptor.getBareMethodName();

		for (Method method : ClassUtils.methods(cls)) {
			if (Objects.equals(method.getName(), bareMethodName)) {
				return method;
			}
		}

		return null;
	}

	@Override

	public void onApplicationStart() {
		server.start();
		log.info("grpc server started. port: {}", server.getPort());
		ThreadUtils.execute("GrpcServer", server::awaitTermination);
	}

	@Override
	public void onApplicationStop() {
		log.warn("shutdown grpc server!");
		server.shutdown();
	}

	public Server getServer() {return this.server;}

	public Map<String, Class<? extends BindableService>> getServiceNameMap() {return this.serviceNameMap;}

	public Map<String, Method> getFullMethodNameMap() {return this.fullMethodNameMap;}
}
