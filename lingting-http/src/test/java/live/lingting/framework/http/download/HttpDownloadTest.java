package live.lingting.framework.http.download;

import live.lingting.framework.exception.DownloadException;
import live.lingting.framework.reflect.ClassField;
import live.lingting.framework.util.ClassUtils;
import live.lingting.framework.util.DigestUtils;
import live.lingting.framework.util.RandomUtils;
import live.lingting.framework.util.StreamUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2024-01-29 16:43
 */
class HttpDownloadTest {

	final URI url = URI.create(
			"https://maven.aliyun.com/repository/central/live/lingting/components/component-validation/0.0.1/component-validation-0.0.1.pom");

	final String filename = "component-validation-0.0.1.pom";

	final String md5 = "2ce519cf7373a533e1fd297edb9ad1c3";

	@Test
	void resolveFilename() throws InvocationTargetException, IllegalAccessException {
		HttpDownload download = HttpDownload.single(url).build();
		ClassField cf = ClassUtils.classField("filename", download.getClass());
		assertNotNull(cf);
		assertEquals(filename, cf.get(download));
	}

	@Test
	void single() throws IOException, InterruptedException, NoSuchAlgorithmException {
		HttpDownload download = HttpDownload.single(url)
			.filename(String.format("%d.%s.s.pom", System.currentTimeMillis(), RandomUtils.nextHex(3)))
			.build();

		assertFalse(download.isStart());
		assertTrue(download.isSuccess());
		assertFalse(download.isFinished());
		assertThrowsExactly(DownloadException.class, download::await);

		HttpDownload await = download.start().await();

		assertEquals(download, await);
		assertTrue(download.isStart());
		assertTrue(download.isSuccess());
		assertTrue(download.isFinished());

		File file = download.getFile();
		System.out.println(file.getAbsolutePath());
		try (FileInputStream stream = new FileInputStream(file)) {
			String string = StreamUtils.toString(stream);
			String md5Hex = DigestUtils.md5Hex(string);
			assertEquals(md5, md5Hex);
		}
		finally {
			file.delete();
		}
	}

	@Test
	void multi() throws IOException, InterruptedException, NoSuchAlgorithmException {
		HttpDownload download = HttpDownload.multi(url)
			.filename(String.format("%d.%s.m.pom", System.currentTimeMillis(), RandomUtils.nextHex(3)))
			.maxShardSize(50)
			.build();

		assertFalse(download.isStart());
		assertTrue(download.isSuccess());
		assertFalse(download.isFinished());
		assertThrowsExactly(DownloadException.class, download::await);

		HttpMultiDownload await = (HttpMultiDownload) download.start().await();

		assertEquals(download, await);
		assertTrue(download.isStart());
		assertTrue(download.isSuccess());
		assertTrue(download.isFinished());

		File file = download.getFile();
		System.out.println(file.getAbsolutePath());
		System.out.printf("%d-%d%n", await.getMaxShard(), await.getFinishedShard());
		assertEquals(await.getMaxShard(), await.getFinishedShard());
		try (FileInputStream stream = new FileInputStream(file)) {
			String string = StreamUtils.toString(stream);
			String md5Hex = DigestUtils.md5Hex(string);
			assertEquals(md5, md5Hex);
		}
		finally {
			file.delete();
		}
	}

}
