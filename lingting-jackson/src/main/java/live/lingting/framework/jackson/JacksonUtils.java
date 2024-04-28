package live.lingting.framework.jackson;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import live.lingting.framework.jackson.module.BooleanModule;
import live.lingting.framework.jackson.module.EnumModule;
import live.lingting.framework.jackson.module.JavaTimeModule;
import live.lingting.framework.jackson.module.MoneyModule;
import live.lingting.framework.jackson.module.RModule;
import live.lingting.framework.jackson.provider.NullSerializerProvider;
import live.lingting.framework.jackson.sensitive.SensitiveModule;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Type;
import java.util.function.Consumer;

/**
 * @author lingting 2021/6/9 14:28
 */
@UtilityClass
public class JacksonUtils {

	@Getter
	static ObjectMapper mapper = defaultConfig(new ObjectMapper());

	public static ObjectMapper defaultConfig(ObjectMapper mapper) {
		// 序列化时忽略未知属性
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// 单值元素可以被设置成 array, 防止处理 ["a"] 为 List<String> 时报错
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		// 有特殊需要转义字符, 不报错
		mapper.enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature());

		// 空对象不报错
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

		// 空值处理
		mapper.setSerializerProvider(new NullSerializerProvider());

		// 布尔处理器
		mapper.registerModule(new BooleanModule());
		// 时间解析器
		mapper.registerModule(new JavaTimeModule());
		// 枚举解析器
		mapper.registerModule(new EnumModule());
		// R 解析器
		mapper.registerModule(new RModule());
		// 脱敏相关
		mapper.registerModule(new SensitiveModule());
		// 金额相关
		mapper.registerModule(new MoneyModule());
		return mapper;
	}

	public static void config(ObjectMapper mapper) {
		JacksonUtils.mapper = mapper;
	}

	public static void config(Consumer<ObjectMapper> consumer) {
		consumer.accept(mapper);
	}

	@SneakyThrows
	public static String toJson(Object obj) {
		return mapper.writeValueAsString(obj);
	}

	@SneakyThrows
	public static <T> T toObj(String json, Class<T> r) {
		return mapper.readValue(json, r);
	}

	@SneakyThrows
	public static <T> T toObj(String json, Type t) {
		return mapper.readValue(json, mapper.constructType(t));
	}

	@SneakyThrows
	public static <T> T toObj(String json, TypeReference<T> t) {
		return mapper.readValue(json, t);
	}

	public static <T> T toObj(String json, TypeReference<T> t, T defaultVal) {
		try {
			return mapper.readValue(json, t);
		}
		catch (Exception e) {
			return defaultVal;
		}
	}

	@SneakyThrows
	public static <T> T toObj(JsonNode node, Class<T> r) {
		return mapper.treeToValue(node, r);
	}

	@SneakyThrows
	public static <T> T toObj(JsonNode node, Type t) {
		return mapper.treeToValue(node, mapper.constructType(t));
	}

	@SneakyThrows
	public static <T> T toObj(JsonNode node, TypeReference<T> t) {
		JavaType javaType = mapper.constructType(t.getType());
		return mapper.treeToValue(node, javaType);
	}

	@SneakyThrows
	public static JsonNode toNode(String json) {
		return mapper.readTree(json);
	}

}
