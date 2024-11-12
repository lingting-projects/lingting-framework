package live.lingting.framework.multipart;


import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2024-09-05 16:35
 */
class MultipartTaskTest {

	@Test
	void test() throws IOException {
		String source = "hello multipart test";
		byte[] bytes = source.getBytes();
		InputStream input = new ByteArrayInputStream(bytes);

		long size = bytes.length;
		long partSize = 3;
		long number = Multipart.calculate(size, partSize);
		Multipart multipart = Multipart.builder().source(input).partSize(partSize).build();

		assertEquals(number, multipart.getParts().size());
		assertEquals(size, multipart.getParts().stream().mapToLong(Part::getSize).sum());
		for (Part part : multipart.getParts()) {
			assertEquals(part.size, part.end - part.start + 1);
		}
		TestMultipartTask task = new TestMultipartTask(multipart);
		assertFalse(task.isStarted());
		task.start().await(Duration.ofSeconds(5));
		assertTrue(task.isCompleted());
		ByteArrayOutputStream output = new ByteArrayOutputStream((int) size);
		task.cache.keySet().stream().sorted().forEach(new Consumer<Long>() {
			@Override

			public void accept(Long i) {
				output.write(task.cache.get(i));
			}
		});
		byte[] merged = output.toByteArray();
		assertArrayEquals(bytes, merged);
		assertEquals(source, new String(merged));
	}

}

class TestMultipartTask extends MultipartTask<TestMultipartTask> {

	final Map<Long, byte[]> cache = new ConcurrentHashMap<>();

	public TestMultipartTask(Multipart multipart) {
		super(multipart);
	}


	@Override
	protected void onPart(Part part) {
		try (InputStream stream = multipart.stream(part)) {
			cache.put(part.index, stream.readAllBytes());
		}
	}

}
