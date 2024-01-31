package live.lingting.framework.properties;

import io.grpc.Metadata;
import lombok.Data;

/**
 * @author lingting 2023-12-14 16:40
 */
@Data
public class SecurityGrpcProperties {

	private String authorizationKey = "Authorization";

	public Metadata.Key<String> authorizationKey() {
		return Metadata.Key.of(getAuthorizationKey(), Metadata.ASCII_STRING_MARSHALLER);
	}

}
