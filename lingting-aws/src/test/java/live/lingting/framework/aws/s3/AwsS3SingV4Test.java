package live.lingting.framework.aws.s3;

import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.value.multi.StringMultiValue;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static live.lingting.framework.aws.s3.AwsS3Utils.HEADER_CONTENT_SHA256;
import static live.lingting.framework.aws.s3.AwsS3Utils.HEADER_DATE;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lingting 2024-09-19 20:37
 */
class AwsS3SingV4Test {

	@SneakyThrows
	@Test
	void test() {
		HttpHeaders headers = HttpHeaders.empty();
		headers.put("Host", "examplebucket.s3.amazonaws.com");
		headers.put("Range", "bytes=0-9");
		headers.put("x-amz-content-sha256", "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
		headers.put("x-amz-date", "20130524T000000Z");

		String bodySha256 = headers.first(HEADER_CONTENT_SHA256);
		LocalDateTime dateTime = AwsS3Utils.parse(headers.first(HEADER_DATE), AwsS3SingV4.DATETIME_FORMATTER);

		StringMultiValue params = new StringMultiValue();

		AwsS3SingV4 sing = AwsS3SingV4.builder()
			.dateTime(dateTime)
			.method("GET")
			.path("/test.txt")
			.headers(headers)
			.bodySha256(bodySha256)
			.params(params)
			.region("us-east-1")
			.ak("AKIAIOSFODNN7EXAMPLE")
			.sk("wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY")
			.bucket("examplebucket")
			.build();

		String request = sing.canonicalRequest();
		assertEquals("""
				GET
				/test.txt

				host:examplebucket.s3.amazonaws.com
				range:bytes=0-9
				x-amz-content-sha256:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
				x-amz-date:20130524T000000Z

				host;range;x-amz-content-sha256;x-amz-date
				e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855""", request);

		String scopeDate = sing.date();
		String scope = sing.scope(scopeDate);

		String date = sing.headers.first(HEADER_DATE);
		String source = sing.source(date, scope, request);
		assertEquals("""
				AWS4-HMAC-SHA256
				20130524T000000Z
				20130524/us-east-1/s3/aws4_request
				7344ae5b7ee6c3e7e6b0fe0640412a37625d1fbfff95c48bbb2dc43964946972""", source);
		String sourceHmacSha = sing.sourceHmacSha(source, scopeDate);
		assertEquals("f0e8bdb87c964420e857bd35b5d6ed310bd44f0170aba48dd91039c6036bdb41", sourceHmacSha);
	}

}
