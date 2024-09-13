package live.lingting.framework.huawei.obs;

import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.http.header.UnmodifiableHttpHeaders;

/**
 * @author lingting 2024-09-13 17:08
 */
public class HuaweiObsHeaders extends UnmodifiableHttpHeaders {

	public HuaweiObsHeaders(HttpHeaders value) {
		super(value.unmodifiable());
	}

	public String multipartUploadId() {
		return first("x-obs-uploadId");
	}

}
