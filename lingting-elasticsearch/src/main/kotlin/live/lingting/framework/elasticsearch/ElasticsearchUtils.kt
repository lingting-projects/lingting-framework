package live.lingting.framework.elasticsearch

import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.json.JsonData
import live.lingting.framework.elasticsearch.annotation.Document
import live.lingting.framework.util.AnnotationUtils
import live.lingting.framework.util.ClassUtils
import live.lingting.framework.util.StringUtils
import org.elasticsearch.client.ResponseException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.SerializedLambda
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author lingting 2023-06-16 11:25
 */
class ElasticsearchUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        private val EF_LAMBDA_CACHE: MutableMap<Class<out ElasticsearchFunction<*, *>?>, SerializedLambda?> = ConcurrentHashMap()

        private val CLS_LAMBDA_CACHE: MutableMap<Class<out ElasticsearchFunction<*, *>?>, Class<*>?> = ConcurrentHashMap()

        private val FIELD_LAMBDA_CACHE: MutableMap<Class<out ElasticsearchFunction<*, *>?>, Field?> = ConcurrentHashMap()
        private val log: Logger = LoggerFactory.getLogger(ElasticsearchUtils::class.java)

        fun <T> getEntityClass(cls: Class<*>?): Class<T> {
            val list = ClassUtils.classArguments(cls!!)
            return list[0] as Class<T>
        }

        fun index(cls: Class<*>): String {
            val document = AnnotationUtils.findAnnotation(cls, Document::class.java)
            // 使用注解指定的
            if (document != null && StringUtils.hasText(document.index)) {
                return document.index
            }
            // 没有指定, 使用下划线的
            val name = cls.simpleName
            return StringUtils.humpToUnderscore(name)
        }

        @Throws(NoSuchMethodException::class, InvocationTargetException::class, IllegalAccessException::class)
        fun <T, R> resolveByReflection(function: ElasticsearchFunction<T, R>): SerializedLambda {
            val fClass: Class<out ElasticsearchFunction<*, *>?> = function.javaClass
            val method = fClass.getDeclaredMethod("writeReplace")
            method.isAccessible = true
            return method.invoke(function) as SerializedLambda
        }

        fun <T, R> resolve(function: ElasticsearchFunction<T, R>): SerializedLambda? {
            val fClass: Class<out ElasticsearchFunction<*, *>?> = function.javaClass
            return EF_LAMBDA_CACHE.computeIfAbsent(fClass) { k: Class<out ElasticsearchFunction<*, *>?>? ->
                try {
                    return@computeIfAbsent resolveByReflection<T, R>(function)
                } catch (e: Exception) {
                    log.error("resolve lambda error!", e)
                    return@computeIfAbsent null
                }
            }
        }

        fun <T, R> resolveClass(function: ElasticsearchFunction<T, R>): Class<T>? {
            val fClass: Class<out ElasticsearchFunction<*, *>?> = function.javaClass
            return CLS_LAMBDA_CACHE.computeIfAbsent(fClass) { k: Class<out ElasticsearchFunction<*, *>?>? ->
                try {
                    val lambda = resolve(function)
                    val implClassName = lambda!!.implClass
                    val className = implClassName.replace("/", ".")
                    return@computeIfAbsent Class.forName(className)
                } catch (e: Exception) {
                    log.error("resolve class by lambda error!", e)
                    return@computeIfAbsent null
                }
            } as Class<T>?
        }

        fun <T, R> resolveField(function: ElasticsearchFunction<T, R>): Field? {
            val fClass: Class<out ElasticsearchFunction<*, *>?> = function.javaClass
            return FIELD_LAMBDA_CACHE.computeIfAbsent(fClass) { k: Class<out ElasticsearchFunction<*, *>?>? ->
                try {
                    val lambda = resolve(function)
                    val aClass = resolveClass(function)

                    val implMethodName = lambda!!.implMethodName

                    val implFieldName = if (implMethodName.startsWith("get")) {
                        implMethodName.substring("get".length)
                    } else if (implMethodName.startsWith("set")) {
                        implMethodName.substring("set".length)
                    } else {
                        implMethodName.substring("is".length)
                    }
                    val fieldName = StringUtils.firstLower(implFieldName)
                    val cf = ClassUtils.classField(fieldName!!, aClass!!)
                    return@computeIfAbsent cf!!.field
                } catch (e: Exception) {
                    log.error("resolve method by lambda error!", e)
                    return@computeIfAbsent null
                }
            }
        }

        fun isVersionConflictException(e: Exception?): Boolean {
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
            return phrase.lowercase(Locale.getDefault()).contains("version_conflict_engine_exception")
        }

        fun fieldValue(`object`: Any): FieldValue {
            return if (`object` is FieldValue) `object` else FieldValue.of(JsonData.of(`object`))
        }

        fun fieldName(func: ElasticsearchFunction<*, *>): String {
            val field = resolveField(func)
            return field!!.name
        }
    }
}
