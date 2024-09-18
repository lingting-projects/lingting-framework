package live.lingting.framework.ali.oss;

import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.http.header.UnmodifiableHttpHeaders;

/**
 * @author lingting 2024-09-18 10:28
 */
public class AliOssHeaders extends UnmodifiableHttpHeaders {

	public AliOssHeaders(HttpHeaders value) {
		super(value.unmodifiable());
	}

}
