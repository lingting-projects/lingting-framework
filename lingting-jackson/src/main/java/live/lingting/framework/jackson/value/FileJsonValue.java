package live.lingting.framework.jackson.value;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import live.lingting.framework.function.ThrowingFunction;
import live.lingting.framework.jackson.JacksonUtils;
import live.lingting.framework.util.StringUtils;
import live.lingting.framework.value.AbstractFileValue;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author lingting 2023-06-05 09:46
 */
public class FileJsonValue<T> extends AbstractFileValue<T> {

	public static final String SUFFIX = ".json";

	public static final ObjectMapper mapper = JacksonUtils.getMapper().copy();

	static {
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	public FileJsonValue(File dir, Object filename) {
		super(dir, filename);
	}

	public FileJsonValue(File file) {
		super(file);
	}

	@Override
	public String fillFilename(Object filename) {
		if (filename instanceof String string && string.endsWith(SUFFIX)) {
			return string;
		}
		return String.format("%s%s", filename, SUFFIX);
	}

	@SneakyThrows
	protected T of(String str, ThrowingFunction<String, T> function) {
		if (StringUtils.hasText(str)) {
			return function.apply(str);
		}
		return null;
	}

	@Override
	@SneakyThrows
	protected T ofClass(String json, Class<T> cls) {
		return of(json, s -> mapper.readValue(s, cls));
	}

	@Override
	@SneakyThrows
	protected String toString(T t) {
		return mapper.writeValueAsString(t);
	}

	public Optional<T> optional(TypeReference<T> reference) {
		return optional(json -> of(json, s -> mapper.readValue(s, reference)));
	}

	public T orElseGet(Supplier<T> supplier, TypeReference<T> reference) throws IOException {
		return orElseGet(supplier, () -> optional(reference));
	}

}
