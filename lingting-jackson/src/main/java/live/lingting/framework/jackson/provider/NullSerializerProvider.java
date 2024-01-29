package live.lingting.framework.jackson.provider;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import live.lingting.framework.jackson.serializer.NullArrayJsonSerializer;
import live.lingting.framework.jackson.serializer.NullMapJsonSerializer;
import live.lingting.framework.jackson.serializer.NullStringJsonSerializer;

import java.io.Serial;
import java.util.Collection;
import java.util.Map;

/**
 * @author lingting
 */
public class NullSerializerProvider extends DefaultSerializerProvider {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * null array 或 list，set 则转 '[]'
	 */
	private static final JsonSerializer<Object> nullArrayJsonSerializer = new NullArrayJsonSerializer();

	/**
	 * null Map 转 '{}'
	 */
	private static final JsonSerializer<Object> nullMapJsonSerializer = new NullMapJsonSerializer();

	/**
	 * null 字符串转 ''
	 */
	private static final JsonSerializer<Object> nullStringJsonSerializer = new NullStringJsonSerializer();

	public static final NullSerializerProvider INSTANCE = new NullSerializerProvider();

	public NullSerializerProvider() {
		super();
	}

	public NullSerializerProvider(NullSerializerProvider src) {
		super(src);
	}

	protected NullSerializerProvider(SerializerProvider src, SerializationConfig config, SerializerFactory f) {
		super(src, config, f);
	}

	@Override
	public DefaultSerializerProvider copy() {
		if (getClass() != NullSerializerProvider.class) {
			return super.copy();
		}
		return new NullSerializerProvider(this);
	}

	@Override
	public NullSerializerProvider createInstance(SerializationConfig config, SerializerFactory jsf) {
		return new NullSerializerProvider(this, config, jsf);
	}

	@Override
	public JsonSerializer<Object> findNullValueSerializer(BeanProperty property) throws JsonMappingException {
		JavaType propertyType = property.getType();
		if (isStringType(propertyType)) {
			return nullStringJsonSerializer;
		}
		else if (isArrayOrCollectionTrype(propertyType)) {
			return nullArrayJsonSerializer;
		}
		else if (isMapType(propertyType)) {
			return nullMapJsonSerializer;
		}
		else {
			return super.findNullValueSerializer(property);
		}
	}

	/**
	 * 是否是 String 类型
	 * @param type JavaType
	 * @return boolean
	 */
	private boolean isStringType(JavaType type) {
		Class<?> clazz = type.getRawClass();
		return String.class.isAssignableFrom(clazz);
	}

	/**
	 * 是否是Map类型
	 * @param type JavaType
	 * @return boolean
	 */
	private boolean isMapType(JavaType type) {
		Class<?> clazz = type.getRawClass();
		return Map.class.isAssignableFrom(clazz);
	}

	/**
	 * 是否是集合类型或者数组
	 * @param type JavaType
	 * @return boolean
	 */
	private boolean isArrayOrCollectionTrype(JavaType type) {
		Class<?> clazz = type.getRawClass();
		return clazz.isArray() || Collection.class.isAssignableFrom(clazz);

	}

}
