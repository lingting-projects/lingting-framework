package live.lingting.framework.util;

import live.lingting.framework.function.ThrowingBiConsumerE;
import live.lingting.framework.function.ThrowingBiFunctionE;
import live.lingting.framework.function.ThrowingConsumerE;
import live.lingting.framework.stream.CloneInputStream;
import live.lingting.framework.stream.FileCloneInputStream;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

import static live.lingting.framework.util.ByteUtils.isLine;
import static live.lingting.framework.util.ByteUtils.trimEndLine;

/**
 * @author lingting
 */
@UtilityClass
public class StreamUtils {

	@Getter
	@Setter
	static int readSize = 1024 * 1024 * 10;

	/**
	 * 读取流, 如果 function 返回 false 则结束读取
	 * @param function 消费读取到的数据, byte[] 数据, 读取长度. 返回false 则结束读取
	 */
	public static void readByFlag(InputStream in, int size,
			ThrowingBiFunctionE<byte[], Integer, Boolean, IOException> function) throws IOException {
		byte[] bytes = new byte[size];
		int len;

		try (in) {
			while (true) {
				len = in.read(bytes);
				// 已读取长度小于1 或者 消费数据, 返回标志位为false
				boolean isBreak = len < 1 || Boolean.FALSE.equals(function.apply(bytes, len));
				if (isBreak) {
					break;
				}
			}
		}

	}

	public static byte[] read(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		write(in, out);
		try {
			return out.toByteArray();
		}
		finally {
			close(out);
		}
	}

	public static void read(InputStream in, ThrowingBiConsumerE<byte[], Integer, IOException> consumer)
			throws IOException {
		read(in, readSize, consumer);
	}

	/**
	 * 读取流
	 * @param in 流
	 * @param size 缓冲区大小
	 * @param consumer 消费读取到的数据, byte[] 数据, 读取长度
	 * @throws IOException 读取异常
	 */
	public static void read(InputStream in, int size, ThrowingBiConsumerE<byte[], Integer, IOException> consumer)
			throws IOException {
		readByFlag(in, size, (bytes, length) -> {
			consumer.accept(bytes, length);
			return true;
		});
	}

	public static void readCopy(InputStream in, ThrowingConsumerE<byte[], IOException> consumer) throws IOException {
		readCopy(in, readSize, consumer);
	}

	public static void readCopy(InputStream in, int size, ThrowingConsumerE<byte[], IOException> consumer)
			throws IOException {
		read(in, size, (bytes, length) -> {
			byte[] copy = Arrays.copyOf(bytes, length);
			consumer.accept(copy);
		});
	}

	public static void write(InputStream in, File file) throws IOException {
		try (OutputStream out = Files.newOutputStream(file.toPath())) {
			write(in, out);
		}
	}

	public static void write(InputStream in, OutputStream out) throws IOException {
		write(in, out, readSize);
	}

	public static void write(InputStream in, OutputStream out, int size) throws IOException {
		read(in, size, (bytes, len) -> out.write(bytes, 0, len));
	}

	public static void write(InputStream in, OutputStream out, long length) throws IOException {
		write(in, out, readSize, length);
	}

	public static void write(InputStream in, OutputStream out, int size, long length) throws IOException {
		AtomicLong atomic = new AtomicLong(0);
		readByFlag(in, size, (bytes, len) -> {
			// 计算剩余字节长度
			long remainLength = length - atomic.get();
			// 计算本次写入的字节长度, 不能大于剩余字节长度
			int writeLength = len > remainLength ? (int) remainLength : len;
			// 写入
			out.write(bytes, 0, writeLength);
			long existLength = atomic.addAndGet(len);
			return existLength < length;
		});
	}

	public static String toString(InputStream in) throws IOException {
		return toString(in, StandardCharsets.UTF_8);
	}

	public static String toString(InputStream in, Charset charset) throws IOException {
		return toString(in, readSize, charset);
	}

	public static String toString(InputStream in, int size, Charset charset) throws IOException {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			write(in, out, size);
			return out.toString(charset);
		}
	}

	/**
	 * 从流中读取 int
	 *
	 * @author lingting 2021-07-22 14:54
	 */
	public static int readInt(InputStream is, int noOfBytes, boolean bigEndian) throws IOException {
		int ret = 0;
		int sv = bigEndian ? ((noOfBytes - 1) * 8) : 0;
		int cnt = bigEndian ? -8 : 8;
		for (int i = 0; i < noOfBytes; i++) {
			ret |= is.read() << sv;
			sv += cnt;
		}
		return ret;
	}

	public static void close(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		}
		catch (Exception e) {
			//
		}
	}

	/**
	 * 克隆文件流
	 * <p color="red">
	 * 注意: 在使用后及时关闭复制流
	 * </p>
	 * @param stream 源流
	 * @return 返回指定数量的从源流复制出来的只读流
	 * @author lingting 2021-04-16 16:18
	 */
	public static FileCloneInputStream clone(InputStream stream) throws IOException {
		return clone(stream, readSize);
	}

	public static FileCloneInputStream clone(InputStream input, int size) throws IOException {
		File file = FileUtils.createTemp(".clone", CloneInputStream.TEMP_DIR);
		try (FileOutputStream output = new FileOutputStream(file)) {
			write(input, output, size);
		}
		return new FileCloneInputStream(file);
	}

	/**
	 * 读取流, 当读取完一行数据时, 消费该数据
	 * @param in 流
	 * @param charset 字符集
	 * @param consumer 行数据消费, int: 行索引
	 * @throws IOException 异常
	 */
	public static void readLine(InputStream in, Charset charset, BiConsumer<Integer, String> consumer)
			throws IOException {
		readLine(in, charset, readSize, consumer);
	}

	/**
	 * 读取流, 当读取完一行数据时, 消费该数据
	 * @param in 流
	 * @param charset 字符集
	 * @param consumer 行数据消费, int: 行索引
	 * @throws IOException 异常
	 */
	public static void readLine(InputStream in, Charset charset, int size, BiConsumer<Integer, String> consumer)
			throws IOException {
		readLine(in, size, (index, bytes) -> {
			String string = new String(bytes, charset);
			String clean = StringUtils.cleanBom(string);
			consumer.accept(index, clean);
		});
	}

	/**
	 * 读取流, 当读取完一行数据时, 消费该数据
	 * @param in 流
	 * @param consumer 行数据消费, int: 行索引
	 * @throws IOException 异常
	 */
	public static void readLine(InputStream in, BiConsumer<Integer, byte[]> consumer) throws IOException {
		readLine(in, readSize, consumer);
	}

	/**
	 * 读取流, 当读取完一行数据时, 消费该数据
	 * @param in 流
	 * @param size 一次读取数据大小
	 * @param consumer 行数据消费, int: 行索引
	 * @throws IOException 异常
	 */
	public static void readLine(InputStream in, int size, BiConsumer<Integer, byte[]> consumer) throws IOException {
		BiConsumer<Integer, List<Byte>> doConsumer = (index, list) -> {
			byte[] bytes = trimEndLine(list);
			consumer.accept(index, bytes);
		};

		List<Byte> list = new ArrayList<>();
		AtomicInteger atomic = new AtomicInteger(0);

		read(in, size, (bytes, length) -> {
			for (int i = 0; i < length; i++) {
				byte b = bytes[i];
				list.add(b);
				// 如果是一整行数据, 则消费
				if (isLine(list)) {
					// 获取行索引, 并自增
					int index = atomic.getAndIncrement();
					doConsumer.accept(index, list);
					// 消费完毕, 结算
					list.clear();
				}
			}
		});

		// 消费剩余的数据
		if (!list.isEmpty()) {
			int index = atomic.get();
			doConsumer.accept(index, list);
		}

	}

}
