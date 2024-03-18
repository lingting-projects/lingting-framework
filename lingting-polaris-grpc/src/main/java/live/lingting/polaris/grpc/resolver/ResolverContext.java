
package live.lingting.polaris.grpc.resolver;

import com.tencent.polaris.api.pojo.ServiceKey;
import com.tencent.polaris.client.api.SDKContext;

import java.net.URI;

public class ResolverContext {

	private URI targetUri;

	private SDKContext context;

	private ServiceKey sourceService;

	public URI getTargetUri() {
		return targetUri;
	}

	public SDKContext getContext() {
		return context;
	}

	public ServiceKey getSourceService() {
		return sourceService;
	}

	public static ResolverContextBuilder builder() {
		return new ResolverContextBuilder();
	}

	public static final class ResolverContextBuilder {

		private URI targetUri;

		private SDKContext context;

		private ServiceKey sourceService;

		private ResolverContextBuilder() {
		}

		public ResolverContextBuilder targetUri(URI targetUri) {
			this.targetUri = targetUri;
			return this;
		}

		public ResolverContextBuilder context(SDKContext context) {
			this.context = context;
			return this;
		}

		public ResolverContextBuilder sourceService(ServiceKey sourceService) {
			this.sourceService = sourceService;
			return this;
		}

		public ResolverContext build() {
			ResolverContext resolverContext = new ResolverContext();
			resolverContext.context = this.context;
			resolverContext.targetUri = this.targetUri;
			resolverContext.sourceService = this.sourceService;
			return resolverContext;
		}

	}

}
