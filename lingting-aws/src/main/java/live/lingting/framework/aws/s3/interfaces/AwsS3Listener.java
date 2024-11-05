package live.lingting.framework.aws.s3.interfaces;

import live.lingting.framework.aws.s3.AwsS3Request;
import live.lingting.framework.http.HttpResponse;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.value.multi.StringMultiValue;

import java.time.LocalDateTime;

/**
 * @author lingting 2024/11/5 14:47
 */
public interface AwsS3Listener {

	void onFailed(AwsS3Request request, HttpResponse response);

	void onAuthorization(AwsS3Request request, HttpHeaders headers, StringMultiValue params, LocalDateTime now);

}
