package live.lingting.framework.http.download;

import live.lingting.framework.util.DigestUtils;
import live.lingting.framework.util.FileUtils;
import live.lingting.framework.util.StreamUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2024-01-29 16:43
 */
@Slf4j
class HttpDownloadTest {

	final URI url = URI.create(
			"https://maven.aliyun.com/repository/central/live/lingting/components/component-validation/0.0.1/component-validation-0.0.1.pom");

	final String md5 = "2ce519cf7373a533e1fd297edb9ad1c3";

	@Test
	void single() throws IOException, NoSuchAlgorithmException {
		HttpDownload download = HttpDownload.single(url).build();

		assertFalse(download.isStart());
		assertFalse(download.isSuccess());
		assertFalse(download.isFinished());
		assertThrowsExactly(IllegalStateException.class, download::await);

		HttpDownload await = download.start().await();

		if (!download.isSuccess()) {
			log.error("error", download.getEx());
		}

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
			FileUtils.delete(file);
		}
	}

	@Test
	void multi() throws IOException, NoSuchAlgorithmException {
		HttpDownload download = HttpDownload.multi(url).partSize(50).build();

		assertFalse(download.isStart());
		assertFalse(download.isSuccess());
		assertFalse(download.isFinished());
		assertThrowsExactly(IllegalStateException.class, download::await);
		HttpDownload await = download.start().await();

		if (!download.isSuccess()) {
			log.error("error", download.getEx());
		}

		assertEquals(download, await);
		assertTrue(download.isStart());
		assertTrue(download.isSuccess());
		assertTrue(download.isFinished());

		File file = download.getFile();
		System.out.println(file.getAbsolutePath());
		File temp = FileUtils.createTemp(".2");
		try {
			download.transferTo(temp);
			assertFile(file);
			assertFile(temp);
		}
		finally {
			FileUtils.delete(file);
			FileUtils.delete(temp);
		}
	}

	void assertFile(File target) throws NoSuchAlgorithmException, IOException {
		try (FileInputStream stream = new FileInputStream(target)) {
			String string = StreamUtils.toString(stream);
			String md5Hex = DigestUtils.md5Hex(string);
			assertEquals(md5, md5Hex);
		}
	}

}
