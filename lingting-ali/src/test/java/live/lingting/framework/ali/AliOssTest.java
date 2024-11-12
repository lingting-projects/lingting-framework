package live.lingting.framework.ali;

import live.lingting.framework.ali.exception.AliException;
import live.lingting.framework.ali.properties.AliOssProperties;
import live.lingting.framework.aws.s3.AwsS3MultipartTask;
import live.lingting.framework.aws.s3.response.AwsS3MultipartItem;
import live.lingting.framework.http.download.HttpDownload;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.id.Snowflake;
import live.lingting.framework.multipart.Multipart;
import live.lingting.framework.multipart.PartTask;
import live.lingting.framework.thread.Async;
import live.lingting.framework.util.CollectionUtils;
import live.lingting.framework.util.DigestUtils;
import live.lingting.framework.util.StreamUtils;
import live.lingting.framework.value.multi.StringMultiValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.util.List;

import static live.lingting.framework.aws.s3.AwsS3Utils.MULTIPART_MIN_PART_SIZE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2024-09-18 14:29
 */
@EnabledIfSystemProperty(named = "framework.ali.oss.test", matches = "true")
class AliOssTest {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(AliOssTest.class);
	AliSts sts;

	AliOssProperties properties;

	@BeforeEach
	void before() {
		sts = AliBasic.sts();
		properties = AliBasic.ossProperties();
	}


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
		HttpHeaders head = ossObject.head();
		assertNotNull(head);
		assertEquals(bytes.length, head.contentLength());
		assertTrue("\"%s\"".formatted(hex).equalsIgnoreCase(head.etag()));
		HttpDownload await = HttpDownload.single(ossObject.publicUrl()).build().start().await();
		String string = StreamUtils.toString(Files.newInputStream(await.getFile().toPath()));
		assertEquals(source, string);
		ossObject.delete();
	}


	@Test
	void multipart() {
		AliOssBucket ossBucket = sts.ossBucket(properties);
		AliOssObject bo = ossBucket.use("ali/b_t");
		String uploadId = bo.multipartInit();
		List<AwsS3MultipartItem> bm = ossBucket.multipartList(r -> {
			StringMultiValue params = r.getParams();
			params.add("prefix", bo.getKey());
		});
		assertFalse(bm.isEmpty());
		assertTrue(bm.stream().anyMatch(i -> i.key().equals(bo.getKey())));
		assertTrue(bm.stream().anyMatch(i -> i.uploadId().equals(uploadId)));

		List<AwsS3MultipartItem> list = ossBucket.multipartList();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(i -> {
				AliOssObject ossObject = ossBucket.use(i.key());
				ossObject.multipartCancel(i.uploadId());
			});
		}

		Snowflake snowflake = new Snowflake(0, 1);
		String key = "ali/m_" + snowflake.nextId();
		AliOssObject ossObject = sts.ossObject(properties, key);
		assertThrows(AliException.class, ossObject::head);
		String source = "hello world\n".repeat(10000);
		byte[] bytes = source.getBytes();
		String hex = DigestUtils.md5Hex(bytes);
		AwsS3MultipartTask task = assertDoesNotThrow(
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
		HttpHeaders head = ossObject.head();
		assertNotNull(head);
		assertEquals(bytes.length, head.contentLength());
		HttpDownload await = HttpDownload.single(ossObject.publicUrl()).build().start().await();
		String string = StreamUtils.toString(Files.newInputStream(await.getFile().toPath()));
		assertEquals(source, string);
		assertEquals(hex, DigestUtils.md5Hex(string));
		ossObject.delete();
	}

}
