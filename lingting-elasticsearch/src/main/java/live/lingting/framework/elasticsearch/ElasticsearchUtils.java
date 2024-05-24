package live.lingting.framework.elasticsearch;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.json.JsonData;
import live.lingting.framework.elasticsearch.annotation.Document;
import live.lingting.framework.reflect.ClassField;
import live.lingting.framework.util.AnnotationUtils;
import live.lingting.framework.util.ClassUtils;
import live.lingting.framework.util.StringUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.StatusLine;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lingting 2023-06-16 11:25
 */
@Slf4j
@UtilityClass
@SuppressWarnings({ "unchecked", "java:S3011" })
public class ElasticsearchUtils {

	private static final Map<Class<? extends ElasticsearchFunction>, SerializedLambda> EF_LAMBDA_CACHE = new ConcurrentHashMap<>();

	private static final Map<Class<? extends ElasticsearchFunction>, Class<?>> CLS_LAMBDA_CACHE = new ConcurrentHashMap<>();

	private static final Map<Class<? extends ElasticsearchFunction>, Field> FIELD_LAMBDA_CACHE = new ConcurrentHashMap<>();

	public static <T> Class<T> getEntityClass(Class<?> cls) {
		List<Class<?>> list = ClassUtils.classArguments(cls);
		return (Class<T>) list.get(0);
	}

	public static String index(Class<?> cls) {
		Document document = AnnotationUtils.findAnnotation(cls, Document.class);
		// 使用注解指定的
		if (document != null && StringUtils.hasText(document.index())) {
			return document.index();
		}
		// 没有指定, 使用下划线的
		String name = cls.getSimpleName();
		return StringUtils.humpToUnderscore(name);
	}

	static <T, R> SerializedLambda resolveByReflection(ElasticsearchFunction<T, R> function)
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Class<? extends ElasticsearchFunction> fClass = function.getClass();
		Method method = fClass.getDeclaredMethod("writeReplace");
		method.setAccessible(true);
		return (SerializedLambda) method.invoke(function);

	}

	public static <T, R> SerializedLambda resolve(ElasticsearchFunction<T, R> function) {
		Class<? extends ElasticsearchFunction> fClass = function.getClass();
		return EF_LAMBDA_CACHE.computeIfAbsent(fClass, k -> {
			try {
				return resolveByReflection(function);
			}
			catch (Exception e) {
				log.error("resolve lambda error!", e);
				return null;
			}
		});
	}

	public static <T, R> Class<T> resolveClass(ElasticsearchFunction<T, R> function) {
		Class<? extends ElasticsearchFunction> fClass = function.getClass();
		return (Class<T>) CLS_LAMBDA_CACHE.computeIfAbsent(fClass, k -> {
			try {
				SerializedLambda lambda = resolve(function);
				String implClassName = lambda.getImplClass();
				String className = implClassName.replace("/", ".");
				return Class.forName(className);
			}
			catch (Exception e) {
				log.error("resolve class by lambda error!", e);
				return null;
			}

		});
	}

	public static <T, R> Field resolveField(ElasticsearchFunction<T, R> function) {
		Class<? extends ElasticsearchFunction> fClass = function.getClass();
		return FIELD_LAMBDA_CACHE.computeIfAbsent(fClass, k -> {
			try {
				SerializedLambda lambda = resolve(function);
				Class<T> aClass = resolveClass(function);

				String implMethodName = lambda.getImplMethodName();
				String implFieldName;

				if (implMethodName.startsWith("get")) {
					implFieldName = implMethodName.substring("get".length());
				}
				else if (implMethodName.startsWith("set")) {
					implFieldName = implMethodName.substring("set".length());
				}
				else {
					implFieldName = implMethodName.substring("is".length());
				}
				String fieldName = StringUtils.firstLower(implFieldName);
				ClassField cf = ClassUtils.classField(fieldName, aClass);
				return cf.field();
			}
			catch (Exception e) {
				log.error("resolve method by lambda error!", e);
				return null;
			}

		});
	}

	public static boolean isVersionConflictException(Exception e) {
		if (!(e instanceof ResponseException)) {
			return false;
		}

		Response response = ((ResponseException) e).getResponse();

		StatusLine line = response.getStatusLine();
		int statusCode = line.getStatusCode();

		if (statusCode != 409) {
			return false;
		}

		String phrase = line.getReasonPhrase();

		if (!StringUtils.hasText(phrase)) {
			return false;
		}

		// type为版本冲突
		return phrase.toLowerCase().contains("version_conflict_engine_exception");
	}

	public static FieldValue fieldValue(Object object) {
		return object instanceof FieldValue fv ? fv : FieldValue.of(JsonData.of(object));
	}

	public static String fieldName(ElasticsearchFunction<?, ?> func) {
		Field field = resolveField(func);
		return field.getName();
	}

}
