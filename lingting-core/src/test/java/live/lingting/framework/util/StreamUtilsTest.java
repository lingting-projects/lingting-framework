package live.lingting.framework.util;

import live.lingting.framework.stream.CloneInputStream;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lingting 2023-12-22 11:56
 */
class StreamUtilsTest {

	final String line1 = "1\r\n2\n3";

	final String line2 = "1\t\r\n";

	final String line3 = "这是一行文本\n这是第二行文本\r\neng";

	ByteArrayInputStream of(String string) {
		return new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8));
	}

	@Test
	void readLine() throws IOException {
		StreamUtils.readLine(of(line1), StandardCharsets.UTF_8, (index, line) -> {
			switch (index) {
				case 0:
					assertEquals("1", line);
					break;
				case 1:
					assertEquals("2", line);
					break;
				default:
					assertEquals("3", line);
					break;
			}
		});
		StreamUtils.readLine(of(line2), StandardCharsets.UTF_8, (index, line) -> {
			assertEquals(0, index);
			assertEquals("1\t", line);
		});
		StreamUtils.readLine(of(line3), StandardCharsets.UTF_8, (index, line) -> {
			switch (index) {
				case 0:
					assertEquals("这是一行文本", line);
					break;
				case 1:
					assertEquals("这是第二行文本", line);
					break;
				default:
					assertEquals("eng", line);
					break;
			}
		});
	}

	@Test
	void testClone() throws IOException {
		CloneInputStream clone = StreamUtils.clone(of(line3));
		assertEquals(line3, StreamUtils.toString(clone));
		InputStream copy = clone.copy();
		assertEquals(line3, StreamUtils.toString(copy));
	}

	@Test
	void testWriteLength() throws IOException {
		ByteArrayInputStream source = of(line3);
		source.reset();
		long length = source.available() / 2;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		StreamUtils.write(source, out, length);
		assertEquals(length, out.size());
	}

}
