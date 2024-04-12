
package live.lingting.polaris.grpc.util;

import com.tencent.polaris.api.pojo.RouteArgument;
import com.tencent.polaris.ratelimit.api.rpc.Argument;
import com.tencent.polaris.ratelimit.api.rpc.QuotaResponse;
import io.grpc.ClientInterceptor;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import live.lingting.polaris.grpc.client.MetadataClientInterceptor;
import live.lingting.polaris.grpc.ratelimit.PolarisRateLimitServerInterceptor;
import live.lingting.polaris.grpc.server.MetadataServerInterceptor;
import lombok.experimental.UtilityClass;

import java.util.Iterator;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
@UtilityClass
public class PolarisHelper {

	/**
	 * {@link PolarisLabelsInject} 用户自定义的 PolarisLabelsInject 实现，可以在处理每次流量时，通过
	 * {@link PolarisLabelsInject#modifyRoute(Set)}} 或者
	 * {@link PolarisLabelsInject#modifyRateLimit(Set)} 注入本次流量的标签信息
	 */
	private static PolarisLabelsInject inject;

	static {
		ServiceLoader<PolarisLabelsInject> serviceLoader = ServiceLoader.load(PolarisLabelsInject.class);
		Iterator<PolarisLabelsInject> iterator = serviceLoader.iterator();
		inject = Optional.ofNullable(iterator.hasNext() ? iterator.next() : null).orElse(new PolarisLabelsInject() {
			@Override
			public Set<RouteArgument> modifyRoute(Set<RouteArgument> arguments) {
				return arguments;
			}

			@Override
			public Set<Argument> modifyRateLimit(Set<Argument> arguments) {
				return arguments;
			}
		});
	}

	public static PolarisLabelsInject getLabelsInject() {
		return inject;
	}

	/**
	 * 调用此方法注入用户自定义的 PolarisLabelsInject
	 * @param inject {@link PolarisLabelsInject}
	 */
	public static void setLabelsInject(PolarisLabelsInject inject) {
		PolarisHelper.inject = inject;
	}

	public static ClientInterceptor buildMetadataClientInterceptor() {
		return new MetadataClientInterceptor(s -> true);
	}

	public static ClientInterceptor buildMetadataClientInterceptor(Predicate<String> predicate) {
		return new MetadataClientInterceptor(predicate);
	}

	public static ServerInterceptor buildMetadataServerInterceptor() {
		return new MetadataServerInterceptor();
	}

	/**
	 * 使用 builder 模式开启 gRPC 的限流能力
	 * @return {@link PolarisRateLimitInterceptorBuilder}
	 */
	public static PolarisRateLimitInterceptorBuilder buildRateLimitInterceptor() {
		return new PolarisRateLimitInterceptorBuilder();
	}

	public static class PolarisRateLimitInterceptorBuilder {

		private BiFunction<QuotaResponse, String, Status> rateLimitCallback = (quotaResponse,
				method) -> Status.UNAVAILABLE.withDescription("rate-limit exceeded (server side)");

		private PolarisRateLimitInterceptorBuilder() {
		}

		/**
		 * 当限流触发时，用户自定义的限流结果返回器
		 * @param rateLimitCallback {@link BiFunction<QuotaResponse, String, Status>}
		 * @return {@link PolarisRateLimitInterceptorBuilder}
		 */
		public PolarisRateLimitInterceptorBuilder rateLimitCallback(
				BiFunction<QuotaResponse, String, Status> rateLimitCallback) {
			this.rateLimitCallback = rateLimitCallback;
			return this;
		}

		public PolarisRateLimitServerInterceptor build() {
			PolarisRateLimitServerInterceptor polarisRateLimitInterceptor = new PolarisRateLimitServerInterceptor();
			polarisRateLimitInterceptor.setRateLimitCallback(this.rateLimitCallback);
			return polarisRateLimitInterceptor;
		}

	}

}
