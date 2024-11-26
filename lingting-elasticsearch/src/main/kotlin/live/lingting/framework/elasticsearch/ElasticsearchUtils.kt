package live.lingting.framework.elasticsearch

import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.json.JsonData
import java.lang.invoke.SerializedLambda
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap
import live.lingting.framework.util.ClassUtils
import live.lingting.framework.util.StringUtils
import org.elasticsearch.client.ResponseException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author lingting 2023-06-16 11:25
 */
@Suppress("UNCHECKED_CAST")
object ElasticsearchUtils {
    private val EF_LAMBDA_CACHE: MutableMap<Class<out EFunction<*, *>>, SerializedLambda?> = ConcurrentHashMap()

    private val CLS_LAMBDA_CACHE: MutableMap<Class<out EFunction<*, *>>, Class<*>?> = ConcurrentHashMap()

    private val FIELD_LAMBDA_CACHE: MutableMap<Class<out EFunction<*, *>>, Field?> = ConcurrentHashMap()

    private val log: Logger = LoggerFactory.getLogger(ElasticsearchUtils::class.java)

    @JvmStatic
    fun <T> getEntityClass(cls: Class<*>): Class<T> {
        val list = ClassUtils.classArguments(cls)
        return list[0] as Class<T>
    }

    @JvmStatic
    fun <T, R> resolveByReflection(function: EFunction<T, R>): SerializedLambda {
        val fClass: Class<out EFunction<*, *>> = function.javaClass
        val method = fClass.getDeclaredMethod("writeReplace")
        method.isAccessible = true
        return method.invoke(function) as SerializedLambda
    }

    @JvmStatic
    fun <T, R> resolve(function: EFunction<T, R>): SerializedLambda? {
        val fClass: Class<out EFunction<*, *>> = function.javaClass
        return EF_LAMBDA_CACHE.computeIfAbsent(fClass) { k ->
            try {
                return@computeIfAbsent resolveByReflection<T, R>(function)
            } catch (e: Exception) {
                log.error("resolve lambda error!", e)
                return@computeIfAbsent null
            }
        }
    }

    @JvmStatic
    fun <T, R> resolveClass(function: EFunction<T, R>): Class<T>? {
        val fClass: Class<out EFunction<*, *>> = function.javaClass
        return CLS_LAMBDA_CACHE.computeIfAbsent(fClass) { k ->
            try {
                val lambda = resolve(function)
                if (lambda == null) {
                    return@computeIfAbsent null
                }
                val implClassName = lambda.implClass
                val className = implClassName.replace("/", ".")
                return@computeIfAbsent Class.forName(className)
            } catch (e: Exception) {
                log.error("resolve class by lambda error!", e)
                return@computeIfAbsent null
            }
        } as Class<T>
    }

    @JvmStatic
    fun <T, R> resolveField(function: EFunction<T, R>): Field? {
        val fClass: Class<out EFunction<*, *>> = function.javaClass
        return FIELD_LAMBDA_CACHE.computeIfAbsent(fClass) { k ->
            try {
                val lambda = resolve(function)
                val aClass = resolveClass(function)

                if (lambda == null || aClass == null) {
                    return@computeIfAbsent null
                }

                val implMethodName = lambda.implMethodName

                val implFieldName = if (implMethodName.startsWith("get")) {
                    implMethodName.substring("get".length)
                } else if (implMethodName.startsWith("set")) {
                    implMethodName.substring("set".length)
                } else {
                    implMethodName.substring("is".length)
                }
                val fieldName = StringUtils.firstLower(implFieldName)
                val cf = ClassUtils.classField(fieldName, aClass)
                return@computeIfAbsent cf?.field
            } catch (e: Exception) {
                log.error("resolve method by lambda error!", e)
                return@computeIfAbsent null
            }
        }
    }

    @JvmStatic
    fun isVersionConflictException(e: Throwable): Boolean {
        if (e !is ResponseException) {
            return false
        }

        val response = e.response

        val line = response.statusLine
        val statusCode = line.statusCode

        if (statusCode != 409) {
            return false
        }

        val phrase = line.reasonPhrase

        if (!StringUtils.hasText(phrase)) {
            return false
        }

        // type为版本冲突
        return phrase.lowercase().contains("version_conflict_engine_exception")
    }

    @JvmStatic
    fun <T> fieldValue(`object`: T): FieldValue {
        return `object` as? FieldValue ?: FieldValue.of(JsonData.of(`object`))
    }

    @JvmStatic
    fun fieldName(func: EFunction<*, *>): String {
        val field = resolveField(func)
        return field?.name ?: ""
    }

}
