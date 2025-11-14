package live.lingting.framework.elasticsearch.util

import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch._types.SortOptions
import co.elastic.clients.json.JsonData
import live.lingting.framework.api.PaginationParams
import live.lingting.framework.elasticsearch.EFunction
import live.lingting.framework.elasticsearch.builder.SearchBuilder
import live.lingting.framework.reflect.LambdaMeta
import live.lingting.framework.util.ClassUtils
import live.lingting.framework.util.StringUtils
import org.elasticsearch.client.ResponseException

/**
 * @author lingting 2023-06-16 11:25
 */
@Suppress("UNCHECKED_CAST")
object ElasticsearchUtils {

    @JvmStatic
    fun <T> getEntityClass(cls: Class<*>): Class<T> {
        val list = ClassUtils.classArguments(cls)
        return list[0] as Class<T>
    }

    @JvmStatic
    fun <T, R> resolve(function: EFunction<T, R>): LambdaMeta? {
        return LambdaMeta.of(function)
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
        return phrase.lowercase().contains("conflict")
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

    fun PaginationParams.toOptions(): List<SortOptions> {
        val builder = SearchBuilder<Any>()
        for (sort in sorts) {
            val field = if (sort.field.startsWith("_")) sort.field else StringUtils.underscoreToHump(sort.field)
            builder.sort(field, sort.desc)
        }
        return builder.buildSorts()
    }

}
