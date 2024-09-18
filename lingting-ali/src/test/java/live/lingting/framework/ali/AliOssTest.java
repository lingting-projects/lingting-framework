package live.lingting.framework.ali;

import live.lingting.framework.ali.exception.AliException;
import live.lingting.framework.ali.oss.AliOssHeaders;
import live.lingting.framework.ali.properties.AliOssProperties;
import live.lingting.framework.http.download.HttpDownload;
import live.lingting.framework.id.Snowflake;
import live.lingting.framework.util.DigestUtils;
import live.lingting.framework.util.StreamUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author lingting 2024-09-18 14:29
 */
@Slf4j
 class AliOssTest {

	AliSts sts;

	AliOssProperties properties;

	@BeforeEach
	void before() {
		sts = AliBasic.sts();
		properties = AliBasic.ossProperties();
	}

	@SneakyThrows
	@Test
	void put() {
		Snowflake snowflake = new Snowflake(0, 0);
		String key = "test/" + snowflake.nextId();
		log.info("key: {}", key);
		AliOssObject ossObject = sts.ossObject(properties, key);
		assertThrows(AliException.class, ossObject::head);
		String source = "hello world";
		byte[] bytes = source.getBytes();
		String hex = DigestUtils.md5Hex(bytes);
		assertDoesNotThrow(() -> ossObject.put(new ByteArrayInputStream(bytes)));
		AliOssHeaders head = ossObject.head();
		assertNotNull(head);
		assertEquals(bytes.length, head.contentLength());
		assertEquals("\"%s\"".formatted(hex), head.etag());
		HttpDownload await = HttpDownload.single(ossObject.publicUrl()).build().start().await();
		String string = StreamUtils.toString(Files.newInputStream(await.getFile().toPath()));
		assertEquals(source, string);
		ossObject.delete();
	}

}
