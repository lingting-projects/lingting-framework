package live.lingting.framework.jackson.xml

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import live.lingting.framework.jackson.JacksonUtils
import live.lingting.framework.jackson.wrapper.JacksonWrapper
import java.lang.reflect.Type
import java.util.function.Consumer
import kotlin.reflect.KClass

/**
 * @author lingting 2021/6/9 14:28
 */
@Suppress("UNCHECKED_CAST")
object JacksonXmlUtils {

    @JvmStatic
    var mapper: XmlMapper = JacksonUtils.defaultConfig(XmlMapper())
        set(value) {
            field = value
            wrapper.mapper = value
        }

    val wrapper = JacksonWrapper(mapper)

    @JvmStatic
    fun config(consumer: Consumer<XmlMapper>) = wrapper.config(consumer)

    @JvmStatic
    fun toXml(obj: Any?): String = wrapper.toString(obj)

    @JvmStatic
    fun <T> toObj(xml: String, type: JavaType): T = wrapper.toObj(xml, type)

    fun <T : Any> toObj(xml: String, r: KClass<out T>): T = wrapper.toObj(xml, r)

    @JvmStatic
    fun <T> toObj(xml: String, r: Class<T>): T = wrapper.toObj(xml, r)

    @JvmStatic
    fun <T> toObj(xml: String, t: Type): T = wrapper.toObj(xml, t)

    @JvmStatic
    fun <T> toObj(xml: String, t: TypeReference<T>): T = wrapper.toObj(xml, t)

    @JvmStatic
    fun <T> toObj(xml: String, t: TypeReference<T>, defaultVal: T): T = wrapper.toObj(xml, t, defaultVal)

    @JvmStatic
    fun <T> toObj(node: JsonNode, r: Class<T>): T = wrapper.toObj(node, r)

    @JvmStatic
    fun <T> toObj(node: JsonNode, t: Type): T = wrapper.toObj(node, t)

    @JvmStatic
    fun <T> toObj(node: JsonNode, t: TypeReference<T>): T = wrapper.toObj(node, t)

    @JvmStatic
    fun toNode(xml: String): JsonNode = wrapper.toNode(xml)

}
