package live.lingting.framework.properties;

import io.grpc.Metadata;

/**
 * @author lingting 2023-12-14 16:40
 */
public class SecurityGrpcProperties {

	private String authorizationKey = "Authorization";

	public Metadata.Key<String> authorizationKey() {
		return Metadata.Key.of(getAuthorizationKey(), Metadata.ASCII_STRING_MARSHALLER);
	}

	public String getAuthorizationKey() {return this.authorizationKey;}

	public void setAuthorizationKey(String authorizationKey) {this.authorizationKey = authorizationKey;}
}
