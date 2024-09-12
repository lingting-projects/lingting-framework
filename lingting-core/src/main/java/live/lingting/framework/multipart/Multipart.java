package live.lingting.framework.multipart;

import live.lingting.framework.stream.RandomAccessInputStream;
import live.lingting.framework.util.FileUtils;
import live.lingting.framework.util.StreamUtils;
import live.lingting.framework.util.StringUtils;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static live.lingting.framework.util.ValueUtils.simpleUuid;

/**
 * @author lingting 2024-09-05 14:47
 */
public class Multipart {

	public static final File TEMP_DIR = FileUtils.createTempDir("multipart");

	public static final String TEMP_SUFFIX = ".source";

	/**
	 * 每个分片的最大大小: byte
	 */
	@Getter
	protected final long partSize;

	/**
	 * 所有分片
	 */
	@Getter
	protected final Collection<Part> parts;

	/**
	 * 唯一标识符
	 */
	@Getter
	protected final String id;

	/**
	 * 原始内容大小: byte
	 */
	@Getter
	protected final long size;

	/**
	 * 原始内容缓存文件
	 */
	@Getter
	protected final File source;

	protected final Map<Long, File> cache;

	public Multipart(InputStream in, long partSize) throws IOException {
		this(null, in, partSize);
	}

	public Multipart(String id, InputStream in, long partSize) throws IOException {
		this(id, FileUtils.createTemp(in, TEMP_SUFFIX, TEMP_DIR), partSize);
	}

	protected Multipart(String id, File source, long partSize) {
		this(id, source, source.length(), partSize);
	}

	public Multipart(long size, long partSize) {
		this(null, size, partSize);
	}

	public Multipart(String id, long size, long partSize) {
		this(id, null, size, partSize);
	}

	protected Multipart(String id, File source, long size, long partSize) {
		this.source = source;
		this.partSize = partSize;
		this.id = StringUtils.hasText(id) ? id : simpleUuid();
		this.size = size;
		this.parts = split(size, partSize);
		this.cache = new ConcurrentHashMap<>(parts.size());
	}

	public static Multipart of(File file, long partSize) throws IOException {
		return of(file, partSize, null);
	}

	public static Multipart of(File file, long partSize, String id) throws IOException {
		File temp = FileUtils.createTemp(TEMP_SUFFIX, TEMP_DIR);
		FileUtils.copy(file, temp, true);
		return new Multipart(id, temp, partSize);
	}

	/**
	 * 生成有限制的 分片
	 * @param id id
	 * @param size 总大小
	 * @param partSize 初始每片大小
	 * @param maxPartSize 每片大小的最大值
	 * @param maxPartCount 最多多少个分片
	 * @return 符合限制的分片
	 */
	public static Multipart of(String id, long size, long partSize, long maxPartSize, long maxPartCount) {
		if (partSize > maxPartSize) {
			throw new IllegalArgumentException("Part size can not be greater than " + maxPartSize);
		}
		long number = calculate(size, partSize);
		if (number > maxPartCount) {
			long step = partSize / 2;
			long newPartSize = partSize + step;
			return of(id, size, newPartSize, maxPartSize, maxPartCount);
		}

		return new Multipart(id, size, partSize);
	}

	/**
	 * 计算对应大小和每个分片大小需要构造多少个分片
	 * @param size 总大小
	 * @param partSize 每个分片大小
	 */
	public static long calculate(long size, long partSize) {
		long l = size / partSize;
		return size % partSize == 0 ? l : l + 1;
	}

	public static Collection<Part> split(long size, long partSize) {
		long number = calculate(size, partSize);
		List<Part> parts = new ArrayList<>((int) number);
		for (long i = 0; i < number; i++) {
			long start = i * partSize;
			long middle = start + partSize - 1;
			long end = middle >= size ? size - 1 : middle;
			Part part = new Part(i, start, end);
			parts.add(part);
		}
		return Collections.unmodifiableCollection(parts);
	}

	public Multipart usePartSize(long partSize) {
		return usePartSize(partSize, null);
	}

	public Multipart usePartSize(long partSize, String id) {
		return new Multipart(id, source, partSize);
	}

	public File file(Part part) {
		return cache.computeIfAbsent(part.index, new Function<Long, File>() {
			@Override
			@SneakyThrows
			public File apply(Long k) {
				File dir = new File(TEMP_DIR, id);
				File temp = FileUtils.createTemp(".part" + k, dir);
				try (RandomAccessInputStream input = new RandomAccessInputStream(source)) {
					input.seek(part.start);
					try (FileOutputStream output = new FileOutputStream(temp)) {
						StreamUtils.write(input, output, part.size);
					}
				}
				return temp;
			}
		});
	}

	public InputStream stream(Part part) throws IOException {
		File file = file(part);
		return Files.newInputStream(file.toPath());
	}

	public void clear() {
		cache.keySet().forEach(this::clear);
	}

	public void clear(Part part) {
		clear(part.index);
	}

	protected void clear(long index) {
		File file = cache.remove(index);
		FileUtils.delete(file);
	}

}
