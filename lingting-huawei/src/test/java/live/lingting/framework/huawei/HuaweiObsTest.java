package live.lingting.framework.huawei;

import live.lingting.framework.http.download.HttpDownload;
import live.lingting.framework.huawei.exception.HuaweiException;
import live.lingting.framework.huawei.obs.HuaweiObsHeaders;
import live.lingting.framework.huawei.properties.HuaweiObsProperties;
import live.lingting.framework.id.Snowflake;
import live.lingting.framework.util.DigestUtils;
import live.lingting.framework.util.StreamUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author lingting 2024-09-13 17:13
 */
@EnabledIfSystemProperty(named = "framework.huawei.obs.test", matches = "true")
class HuaweiObsTest {

	HuaweiIam iam;

	HuaweiObsProperties properties;

	@BeforeEach
	void before() {
		iam = HuaweiBasic.iam();
		properties = HuaweiBasic.obsProperties();
	}

	@SneakyThrows
	@Test
	void put() throws IOException {
		Snowflake snowflake = new Snowflake(0, 0);
		String key = "huawei/obs/test/" + snowflake.nextId();
		HuaweiObsObject obsObject = iam.obsObject(properties, key);
		assertThrows(HuaweiException.class, obsObject::head);
		String source = "hello world";
		byte[] bytes = source.getBytes();
		String hex = DigestUtils.md5Hex(bytes);
		assertDoesNotThrow(() -> obsObject.put(new ByteArrayInputStream(bytes)));
		HuaweiObsHeaders head = obsObject.head();
		assertNotNull(head);
		assertEquals(bytes.length, head.contentLength());
		assertEquals("\"%s\"".formatted(hex), head.etag());
		HttpDownload await = HttpDownload.single(obsObject.publicUrl()).build().start().await();
		String string = StreamUtils.toString(Files.newInputStream(await.getFile().toPath()));
		assertEquals(source, string);
		obsObject.delete();
	}

}
