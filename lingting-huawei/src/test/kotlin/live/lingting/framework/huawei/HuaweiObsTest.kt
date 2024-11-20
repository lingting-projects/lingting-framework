package live.lingting.framework.huawei;

import live.lingting.framework.aws.s3.AwsS3MultipartTask;
import live.lingting.framework.aws.s3.response.AwsS3MultipartItem;
import live.lingting.framework.http.download.HttpDownload;
import live.lingting.framework.huawei.exception.HuaweiException;
import live.lingting.framework.huawei.obs.HuaweiObsHeaders;
import live.lingting.framework.huawei.properties.HuaweiObsProperties;
import live.lingting.framework.id.Snowflake;
import live.lingting.framework.multipart.Multipart;
import live.lingting.framework.multipart.PartTask;
import live.lingting.framework.thread.Async;
import live.lingting.framework.util.CollectionUtils;
import live.lingting.framework.util.DigestUtils;
import live.lingting.framework.util.StreamUtils;
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
 * @author lingting 2024-09-13 17:13
 */
@EnabledIfSystemProperty(named = "framework.huawei.obs.test", matches = "true")
class HuaweiObsTest {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(HuaweiObsTest.class);
	HuaweiIam iam;

	HuaweiObsProperties properties;

	@BeforeEach
	void before() {
		iam = HuaweiBasic.iam();
		properties = HuaweiBasic.obsProperties();
	}


	@Test
	void put() {
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


	@Test
	void multipart() {
		HuaweiObsBucket obsBucket = iam.obsBucket(properties);
		List<AwsS3MultipartItem> items = obsBucket.multipartList();
		if (!CollectionUtils.isEmpty(items)) {
			items.forEach(item -> {
				String k = item.key;
				String v = item.uploadId;
				HuaweiObsObject obsObject = obsBucket.use(k);
				obsObject.multipartCancel(v);
			});
		}

		Snowflake snowflake = new Snowflake(0, 1);
		String key = "huawei/obs/test/" + snowflake.nextId();
		HuaweiObsObject obsObject = iam.obsObject(properties, key);
		assertThrows(HuaweiException.class, obsObject::head);
		String source = "hello world".repeat(90000);
		byte[] bytes = source.getBytes();
		String hex = DigestUtils.md5Hex(bytes);
		AwsS3MultipartTask task = assertDoesNotThrow(
			() -> obsObject.multipart(new ByteArrayInputStream(bytes), 1, new Async(10)));
		assertTrue(task.isStarted());
		task.await();
		if (task.hasFailed()) {
			for (PartTask t : task.tasksFailed()) {
				log.error("multipart error!", t.t);
			}
		}
		assertTrue(task.isCompleted());
		assertFalse(task.hasFailed());
		Multipart multipart = task.multipart;
		assertTrue(multipart.partSize >= MULTIPART_MIN_PART_SIZE);
		HuaweiObsHeaders head = obsObject.head();
		assertNotNull(head);
		assertEquals(bytes.length, head.contentLength());
		assertEquals(task.uploadId, head.multipartUploadId());
		HttpDownload await = HttpDownload.single(obsObject.publicUrl()).build().start().await();
		String string = StreamUtils.toString(Files.newInputStream(await.getFile().toPath()));
		assertEquals(source, string);
		assertEquals(hex, DigestUtils.md5Hex(string));
		obsObject.delete();
	}

}
