package live.lingting.framework.elasticsearch

import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.json.JsonData
import java.util.concurrent.ConcurrentHashMap
import live.lingting.framework.reflect.LambdaMeta
import live.lingting.framework.util.ClassUtils
import live.lingting.framework.util.Slf4jUtils.logger
import live.lingting.framework.util.StringUtils
import org.elasticsearch.client.ResponseException

/**
 * @author lingting 2023-06-16 11:25
 */
@Suppress("UNCHECKED_CAST")
object ElasticsearchUtils {

    private val EF_LAMBDA_CACHE: MutableMap<Class<out EFunction<*, *>>, LambdaMeta?> = ConcurrentHashMap()

    private val log = logger()

    @JvmStatic
    fun <T> getEntityClass(cls: Class<*>): Class<T> {
        val list = ClassUtils.classArguments(cls)
        return list[0] as Class<T>
    }

    @JvmStatic
    fun <T, R> resolve(function: EFunction<T, R>): LambdaMeta? {
        val fClass: Class<out EFunction<*, *>> = function.javaClass
        return EF_LAMBDA_CACHE.computeIfAbsent(fClass) {
            try {
                LambdaMeta.of(function)
            } catch (e: Exception) {
                log.error("resolve lambda error!", e)
                null
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
    fun <T> fieldValue(t: T): FieldValue {
        if (t is FieldValue) {
            return t
        }
        val value = JsonData.of(t)
        return FieldValue.of(value)
    }

    @JvmStatic
    fun fieldName(func: EFunction<*, *>): String {
        return resolve(func)?.field ?: ""
    }

}
