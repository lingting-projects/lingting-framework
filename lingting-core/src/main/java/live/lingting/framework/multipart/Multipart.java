package live.lingting.framework.multipart;

import live.lingting.framework.stream.FileCloneInputStream;
import live.lingting.framework.stream.RandomAccessInputStream;
import live.lingting.framework.util.FileUtils;
import live.lingting.framework.util.StreamUtils;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author lingting 2024-09-05 14:47
 */
public class Multipart {

	public static final File TEMP_DIR = FileUtils.createTempDir("multipart");

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

	protected Multipart(String id, File source, long size, long partSize, Collection<Part> parts) {
		this.id = id;
		this.source = source;
		this.size = size;
		this.parts = parts;
		this.partSize = partSize;
		this.cache = new ConcurrentHashMap<>(parts.size());
	}

	public static MultipartBuilder builder() {
		return new MultipartBuilder();
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
		return new Multipart(id, source, size, partSize, parts);
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

	public FileCloneInputStream stream(Part part) throws IOException {
		File file = file(part);
		return new FileCloneInputStream(file);
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
