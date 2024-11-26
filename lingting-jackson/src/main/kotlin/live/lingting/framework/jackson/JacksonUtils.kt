package live.lingting.framework.jackson

import com.fasterxml.jackson.core.json.JsonReadFeature
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import java.lang.reflect.Type
import java.util.function.Consumer
import kotlin.reflect.KClass
import live.lingting.framework.jackson.module.BooleanModule
import live.lingting.framework.jackson.module.EnumModule
import live.lingting.framework.jackson.module.JavaTimeModule
import live.lingting.framework.jackson.module.MoneyModule
import live.lingting.framework.jackson.module.RModule
import live.lingting.framework.jackson.provider.NullSerializerProvider
import live.lingting.framework.jackson.sensitive.SensitiveModule

/**
 * @author lingting 2021/6/9 14:28
 */
@Suppress("UNCHECKED_CAST")
object JacksonUtils {
    @JvmField
    var mapper: ObjectMapper = defaultConfig(ObjectMapper())

    @JvmField
    var xmlMapper: XmlMapper = defaultConfig(XmlMapper())

    @JvmStatic
    fun <T : ObjectMapper> defaultConfig(mapper: T): T {
        // 序列化时忽略未知属性
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        // 单值元素可以被设置成 array, 防止处理 ["a"] 为 List<String> 时报错
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        // 有特殊需要转义字符, 不报错
        mapper.enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature())

        // 空对象不报错
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)

        // 空值处理
        mapper.setSerializerProvider(NullSerializerProvider())

        // 布尔处理器
        mapper.registerModule(BooleanModule())
        // 时间解析器
        mapper.registerModule(JavaTimeModule())
        // 枚举解析器
        mapper.registerModule(EnumModule())
        // R 解析器
        mapper.registerModule(RModule())
        // 脱敏相关
        mapper.registerModule(SensitiveModule())
        // 金额相关
        mapper.registerModule(MoneyModule())
        return mapper
    }

    @JvmStatic
    fun config(consumer: Consumer<ObjectMapper>) {
        consumer.accept(mapper)
    }

    @JvmStatic
    fun configXml(consumer: Consumer<XmlMapper>) {
        consumer.accept(xmlMapper)
    }

    // region json
    @JvmStatic
    fun toJson(obj: Any?): String {
        return mapper.writeValueAsString(obj)
    }

    fun <T : Any> toObj(json: String, r: KClass<out T>): T = toObj(json, r.java)

    @JvmStatic
    fun <T> toObj(json: String, r: Class<T>): T {
        if (r.isAssignableFrom(String::class.java)) {
            return json as T
        }
        return mapper.readValue(json, r)
    }

    @JvmStatic
    fun <T> toObj(json: String, t: Type): T {
        val type = mapper.constructType(t)
        if (type.rawClass == String::class.java) {
            return json as T
        }
        return mapper.readValue(json, type)
    }

    @JvmStatic
    fun <T> toObj(json: String, t: TypeReference<T>): T {
        return mapper.readValue(json, t)
    }

    @JvmStatic
    fun <T> toObj(json: String, t: TypeReference<T>, defaultVal: T): T {
        return try {
            mapper.readValue(json, t)
        } catch (e: Exception) {
            defaultVal
        }
    }

    fun <T : Any> toObj(node: JsonNode, r: KClass<out T>): T = toObj(node, r.java)

    @JvmStatic
    fun <T> toObj(node: JsonNode, r: Class<T>): T {
        return mapper.treeToValue(node, r)
    }

    @JvmStatic
    fun <T> toObj(node: JsonNode, t: Type): T {
        return mapper.treeToValue(node, mapper.constructType(t))
    }

    @JvmStatic
    fun <T> toObj(node: JsonNode, t: TypeReference<T>): T {
        val javaType = mapper.constructType(t.type)
        return mapper.treeToValue(node, javaType)
    }

    @JvmStatic
    fun toNode(json: String): JsonNode {
        return mapper.readTree(json)
    }

    // endregion

    // region xml
    @JvmStatic
    fun toXml(obj: Any?): String {
        return xmlMapper.writeValueAsString(obj)
    }

    fun <T : Any> xmlToObj(xml: String, r: KClass<out T>): T = xmlToObj(xml, r.java)

    @JvmStatic
    fun <T> xmlToObj(xml: String, r: Class<T>): T {
        if (r.isAssignableFrom(String::class.java)) {
            return xml as T
        }
        return xmlMapper.readValue(xml, r)
    }

    @JvmStatic
    fun <T> xmlToObj(xml: String, t: Type): T {
        val type = xmlMapper.constructType(t)
        if (type.rawClass == String::class.java) {
            return xml as T
        }
        return xmlMapper.readValue(xml, type)
    }

    @JvmStatic
    fun <T> xmlToObj(xml: String, t: TypeReference<T>): T {
        return xmlMapper.readValue(xml, t)
    }

    @JvmStatic
    fun <T> xmlToObj(xml: String, t: TypeReference<T>, defaultVal: T): T {
        return try {
            xmlMapper.readValue(xml, t)
        } catch (e: Exception) {
            defaultVal
        }
    }

    @JvmStatic
    fun <T> xmlToObj(node: JsonNode, r: Class<T>): T {
        return xmlMapper.treeToValue(node, r)
    }

    @JvmStatic
    fun <T> xmlToObj(node: JsonNode, t: Type): T {
        return xmlMapper.treeToValue(node, xmlMapper.constructType(t))
    }

    @JvmStatic
    fun <T> xmlToObj(node: JsonNode, t: TypeReference<T>): T {
        val javaType = xmlMapper.constructType(t.type)
        return xmlMapper.treeToValue(node, javaType)
    }

    @JvmStatic
    fun xmlToNode(xml: String): JsonNode {
        return xmlMapper.readTree(xml)
    }
    // endregion
}

