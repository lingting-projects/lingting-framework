package live.lingting.framework.jackson.wrapper

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.lang.reflect.Type
import java.util.function.Consumer
import kotlin.reflect.KClass

/**
 * @author lingting 2024/12/17 16:23
 */
@Suppress("UNCHECKED_CAST")
class JacksonWrapper<T : ObjectMapper>(var mapper: T) {

    fun config(consumer: Consumer<T>) {
        consumer.accept(mapper)
    }

    // region convert

    fun <T> convert(value: Any?, type: JavaType): T? {
        if (value == null) {
            return null
        }
        if (type.rawClass != null && value.javaClass.isAssignableFrom(type.rawClass)) {
            return value as T
        }
        val bytes = mapper.writeValueAsBytes(value)
        return mapper.readValue(bytes, type)
    }

    fun <T : Any> convert(value: Any?, r: KClass<out T>): T? = convert(value, r.java)

    fun <T> convert(value: Any?, r: Class<T>): T? {
        return convert(value, mapper.constructType(r))
    }

    fun <T> convert(value: Any?, t: Type): T? {
        return convert(value, mapper.constructType(t))
    }

    fun <T> convert(value: Any?, t: TypeReference<T>): T? {
        return convert(value, mapper.constructType(t))
    }

    fun <T> convert(value: Any?, t: TypeReference<T>, defaultVal: T): T? {
        return try {
            convert(value, t)
        } catch (_: Exception) {
            defaultVal
        }
    }

    // endregion

    // region string
    fun toString(obj: Any?): String {
        return mapper.writeValueAsString(obj)
    }

    fun <T> toObj(string: String, type: JavaType): T {
        if (type.rawClass == String::class.java) {
            return string as T
        }
        return mapper.readValue(string, type)
    }

    fun <T : Any> toObj(string: String, r: KClass<out T>): T = toObj(string, r.java)

    fun <T> toObj(string: String, r: Class<T>): T {
        return toObj(string, mapper.constructType(r))
    }

    fun <T> toObj(string: String, t: Type): T {
        return toObj(string, mapper.constructType(t))
    }

    fun <T> toObj(string: String, t: TypeReference<T>): T {
        return toObj(string, mapper.constructType(t))
    }

    fun <T> toObj(string: String, t: TypeReference<T>, defaultVal: T): T {
        return try {
            toObj(string, t)
        } catch (_: Exception) {
            defaultVal
        }
    }

    fun <T : Any> toObj(node: JsonNode, r: KClass<out T>): T = toObj(node, r.java)

    fun <T> toObj(node: JsonNode, r: Class<T>): T {
        return mapper.treeToValue(node, r)
    }

    fun <T> toObj(node: JsonNode, t: Type): T {
        return mapper.treeToValue(node, mapper.constructType(t))
    }

    fun <T> toObj(node: JsonNode, t: TypeReference<T>): T {
        val javaType = mapper.constructType(t.type)
        return mapper.treeToValue(node, javaType)
    }

    fun toNode(string: String): JsonNode {
        return mapper.readTree(string)
    }

    // endregion
}
