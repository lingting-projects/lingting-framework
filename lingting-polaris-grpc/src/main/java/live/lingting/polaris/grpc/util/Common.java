
package live.lingting.polaris.grpc.util;

import com.tencent.polaris.api.pojo.Instance;
import com.tencent.polaris.api.pojo.ServiceKey;
import io.grpc.Attributes.Key;
import io.grpc.Metadata;
import lombok.experimental.UtilityClass;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
@UtilityClass
public class Common {

	public static final Metadata.Key<String> CALLER_SERVICE_KEY = Metadata.Key.of("polaris.request.caller.service",
			Metadata.ASCII_STRING_MARSHALLER);

	public static final Metadata.Key<String> CALLER_NAMESPACE_KEY = Metadata.Key.of("polaris.request.caller.namespace",
			Metadata.ASCII_STRING_MARSHALLER);

	/**
	 * {@link io.grpc.Attributes} 中存放 {@link Instance} 的 key
	 */
	public static final Key<Instance> INSTANCE_KEY = Key.create(Instance.class.getName());

	/**
	 *
	 */
	public static final Key<ServiceKey> SOURCE_SERVICE_INFO = Key.create(ServiceKey.class.getName());

	/**
	 * {@link io.grpc.Attributes} 中存放服务调用者的服务名称信息
	 */
	public static final Key<String> TARGET_SERVICE_KEY = Key.create("POLARIS_SOURCE_SERVICE");

	/**
	 * {@link io.grpc.Attributes} 中存放服务调用者所在的命名空间信息
	 */
	public static final Key<String> TARGET_NAMESPACE_KEY = Key.create("POLARIS_SOURCE_NAMESPACE");

}
