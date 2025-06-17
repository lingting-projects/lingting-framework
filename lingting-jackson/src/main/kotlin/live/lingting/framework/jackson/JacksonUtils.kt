package live.lingting.framework.jackson

import com.fasterxml.jackson.core.json.JsonReadFeature
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import live.lingting.framework.jackson.module.BooleanModule
import live.lingting.framework.jackson.module.DataSizeModule
import live.lingting.framework.jackson.module.EnumModule
import live.lingting.framework.jackson.module.JavaTimeModule
import live.lingting.framework.jackson.module.MoneyModule
import live.lingting.framework.jackson.module.RModule
import live.lingting.framework.jackson.provider.NullSerializerProvider
import live.lingting.framework.jackson.sensitive.SensitiveModule
import live.lingting.framework.jackson.wrapper.JacksonWrapper
import java.lang.reflect.Type
import java.util.function.Consumer
import kotlin.reflect.KClass

/**
 * @author lingting 2021/6/9 14:28
 */
@Suppress("UNCHECKED_CAST")
object JacksonUtils {

    @JvmStatic
    var mapper: ObjectMapper = defaultConfig(ObjectMapper())
        set(value) {
            field = value
            jsonWrapper.mapper = value
        }


    val jsonWrapper = JacksonWrapper(mapper)

    @JvmStatic
    var xmlMapper: XmlMapper = defaultConfig(XmlMapper())
        set(value) {
            field = value
            xmlWrapper.mapper = value
        }

    val xmlWrapper = JacksonWrapper(xmlMapper)

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
        // 数据大小
        mapper.registerModule(DataSizeModule())
        return mapper
    }

    @JvmStatic
    fun config(consumer: Consumer<ObjectMapper>) = jsonWrapper.config(consumer)

    @JvmStatic
    fun configXml(consumer: Consumer<XmlMapper>) = xmlWrapper.config(consumer)

    // region convert

    @JvmStatic
    fun <T> convert(value: Any?, type: JavaType): T? = jsonWrapper.convert(value, type)

    fun <T : Any> convert(value: Any?, r: KClass<out T>): T? = jsonWrapper.convert(value, r)

    @JvmStatic
    fun <T> convert(value: Any?, r: Class<T>): T? = jsonWrapper.convert(value, r)

    @JvmStatic
    fun <T> convert(value: Any?, t: Type): T? = jsonWrapper.convert(value, t)

    @JvmStatic
    fun <T> convert(value: Any?, t: TypeReference<T>): T? = jsonWrapper.convert(value, t)

    @JvmStatic
    fun <T> convert(value: Any?, t: TypeReference<T>, defaultVal: T): T? = jsonWrapper.convert(value, t, defaultVal)

    // endregion

    // region json
    @JvmStatic
    fun toJson(obj: Any?): String = jsonWrapper.toString(obj)

    @JvmStatic
    fun <T> toObj(json: String, type: JavaType): T = jsonWrapper.toObj(json, type)

    fun <T : Any> toObj(json: String, r: KClass<out T>): T = jsonWrapper.toObj(json, r)

    @JvmStatic
    fun <T> toObj(json: String, r: Class<T>): T = jsonWrapper.toObj(json, r)

    @JvmStatic
    fun <T> toObj(json: String, t: Type): T = jsonWrapper.toObj(json, t)

    @JvmStatic
    fun <T> toObj(json: String, t: TypeReference<T>): T = jsonWrapper.toObj(json, t)

    @JvmStatic
    fun <T> toObj(json: String, t: TypeReference<T>, defaultVal: T): T = jsonWrapper.toObj(json, t, defaultVal)

    fun <T : Any> toObj(node: JsonNode, r: KClass<out T>): T = jsonWrapper.toObj(node, r)

    @JvmStatic
    fun <T> toObj(node: JsonNode, r: Class<T>): T = jsonWrapper.toObj(node, r)

    @JvmStatic
    fun <T> toObj(node: JsonNode, t: Type): T = jsonWrapper.toObj(node, t)

    @JvmStatic
    fun <T> toObj(node: JsonNode, t: TypeReference<T>): T = jsonWrapper.toObj(node, t)

    @JvmStatic
    fun toNode(json: String): JsonNode = jsonWrapper.toNode(json)

    // endregion

    // region xml

    @JvmStatic
    fun toXml(obj: Any?): String = xmlWrapper.toString(obj)

    @JvmStatic
    fun <T> xmlToObj(xml: String, type: JavaType): T = xmlWrapper.toObj(xml, type)

    fun <T : Any> xmlToObj(xml: String, r: KClass<out T>): T = xmlWrapper.toObj(xml, r)

    @JvmStatic
    fun <T> xmlToObj(xml: String, r: Class<T>): T = xmlWrapper.toObj(xml, r)

    @JvmStatic
    fun <T> xmlToObj(xml: String, t: Type): T = xmlWrapper.toObj(xml, t)

    @JvmStatic
    fun <T> xmlToObj(xml: String, t: TypeReference<T>): T = xmlWrapper.toObj(xml, t)

    @JvmStatic
    fun <T> xmlToObj(xml: String, t: TypeReference<T>, defaultVal: T): T = xmlWrapper.toObj(xml, t, defaultVal)

    @JvmStatic
    fun <T> xmlToObj(node: JsonNode, r: Class<T>): T = xmlWrapper.toObj(node, r)

    @JvmStatic
    fun <T> xmlToObj(node: JsonNode, t: Type): T = xmlWrapper.toObj(node, t)

    @JvmStatic
    fun <T> xmlToObj(node: JsonNode, t: TypeReference<T>): T = xmlWrapper.toObj(node, t)

    @JvmStatic
    fun xmlToNode(xml: String): JsonNode = xmlWrapper.toNode(xml)

    // endregion

}
