package live.lingting.framework.ali;

import live.lingting.framework.ali.exception.AliException;
import live.lingting.framework.ali.multipart.AliMultipartTask;
import live.lingting.framework.ali.oss.AliOssHeaders;
import live.lingting.framework.ali.properties.AliOssProperties;
import live.lingting.framework.http.download.HttpDownload;
import live.lingting.framework.id.Snowflake;
import live.lingting.framework.multipart.Multipart;
import live.lingting.framework.multipart.PartTask;
import live.lingting.framework.thread.Async;
import live.lingting.framework.util.DigestUtils;
import live.lingting.framework.util.StreamUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;

import static live.lingting.framework.ali.AliUtils.MULTIPART_MIN_PART_SIZE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
		String key = "test/s_" + snowflake.nextId();
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
		assertTrue("\"%s\"".formatted(hex).equalsIgnoreCase(head.etag()));
		HttpDownload await = HttpDownload.single(ossObject.publicUrl()).build().start().await();
		String string = StreamUtils.toString(Files.newInputStream(await.getFile().toPath()));
		assertEquals(source, string);
		ossObject.delete();
	}

	@SneakyThrows
	@Test
	void multipart() {
		Snowflake snowflake = new Snowflake(0, 1);
		String key = "ali/m_" + snowflake.nextId();
		AliOssObject ossObject = sts.ossObject(properties, key);
		assertThrows(AliException.class, ossObject::head);
		String source = "hello world\n".repeat(10000);
		byte[] bytes = source.getBytes();
		String hex = DigestUtils.md5Hex(bytes);
		AliMultipartTask task = assertDoesNotThrow(
				() -> ossObject.multipart(new ByteArrayInputStream(bytes), 1, new Async(10)));
		assertTrue(task.isStarted());
		task.await();
		if (task.hasFailed()) {
			for (PartTask t : task.tasksFailed()) {
				log.error("multipart error!", t.getT());
			}
		}
		assertTrue(task.isCompleted());
		assertFalse(task.hasFailed());
		Multipart multipart = task.getMultipart();
		assertTrue(multipart.getPartSize() >= MULTIPART_MIN_PART_SIZE);
		AliOssHeaders head = ossObject.head();
		assertNotNull(head);
		assertEquals(bytes.length, head.contentLength());
		HttpDownload await = HttpDownload.single(ossObject.publicUrl()).build().start().await();
		String string = StreamUtils.toString(Files.newInputStream(await.getFile().toPath()));
		assertEquals(source, string);
		assertEquals(hex, DigestUtils.md5Hex(string));
		ossObject.delete();
	}

}
