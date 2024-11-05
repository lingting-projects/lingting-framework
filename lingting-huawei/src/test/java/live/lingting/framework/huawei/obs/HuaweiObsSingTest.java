package live.lingting.framework.huawei.obs;

import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.huawei.HuaweiUtils;
import live.lingting.framework.value.multi.StringMultiValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static live.lingting.framework.aws.s3.AwsS3Utils.PAYLOAD_UNSIGNED;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lingting 2024/11/5 13:53
 */
@EnabledIfSystemProperty(named = "framework.huawei.obs.test", matches = "true")
class HuaweiObsSingTest {

	@Test
	void test() {
		HttpHeaders eHeaders = HttpHeaders.empty();
		eHeaders.add("x-obs-content-sha256", PAYLOAD_UNSIGNED);
		StringMultiValue eParams = new StringMultiValue();
		eParams.add("uploads");
		String eDate = "Tue, 5 Nov 2024 06:26:17 GMT";
		HuaweiObsSing.HuaweiObsSingBuilder builder = HuaweiObsSing.builder()
			.ak("ak")
			.sk("sk")
			.method("get")
			.dateTime(HuaweiUtils.parse(eDate))
			.bucket("bucket")
			.headers(eHeaders)
			.params(eParams);

		HuaweiObsSing sing = builder.build();

		assertEquals("""
				GET


				Tue, 5 Nov 2024 06:26:17 GMT
				x-obs-content-sha256:UNSIGNED-PAYLOAD
				/bucket/?uploads""", sing.source());
		assertEquals("OBS ak:DWeWvzUw0RaQpSPJCOTQKxQyBB8=", sing.calculate());
	}

}
