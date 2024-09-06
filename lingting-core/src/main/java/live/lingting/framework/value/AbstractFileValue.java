package live.lingting.framework.value;

import live.lingting.framework.function.ThrowingFunction;
import live.lingting.framework.util.FileUtils;
import live.lingting.framework.util.StreamUtils;
import live.lingting.framework.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author lingting 2023-05-23 09:12
 */
@Slf4j
public abstract class AbstractFileValue<T> {

	protected final File file;

	protected T value;

	protected AbstractFileValue(File dir, Object filename) {
		String filledFilename = fillFilename(filename);
		this.file = new File(dir, filledFilename);
	}

	protected AbstractFileValue(File file) {
		this.file = file;
	}

	/**
	 * 文件名处理, 用于调整后缀
	 * @return 返回真实的文件名
	 */
	public abstract String fillFilename(Object filename);

	protected abstract T ofClass(String str, Class<T> cls);

	protected abstract String toString(T t);

	public Optional<T> optional(ThrowingFunction<String, T> function) {
		if (!file.exists()) {
			return Optional.empty();
		}
		if (value != null) {
			return Optional.of(value);
		}
		try (InputStream in = Files.newInputStream(file.toPath())) {
			String string = StreamUtils.toString(in);
			if (!StringUtils.hasText(string)) {
				value = null;
			}
			else {
				value = function.apply(string);
			}
			return Optional.ofNullable(value);
		}
		catch (Throwable e) {
			log.error("解析文件内容异常! 文件: {}", file.getAbsolutePath(), e);
			return Optional.empty();
		}
	}

	public Optional<T> optional(Class<T> cls) {
		return optional(str -> ofClass(str, cls));
	}

	public void set(T t) throws IOException {
		value = t;
		FileUtils.createFile(file);
		try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
			String str = toString(t);
			writer.write(str);
		}
	}

	public T orElseGet(Supplier<T> supplier, Class<T> cls) throws IOException {
		return orElseGet(supplier, () -> optional(cls));
	}

	public T orElseGet(Supplier<T> supplier, Supplier<Optional<T>> optionalSupplier) throws IOException {
		Optional<T> optional = optionalSupplier.get();
		if (optional.isPresent()) {
			return optional.get();
		}
		T t = supplier.get();
		set(t);
		return t;
	}

}
