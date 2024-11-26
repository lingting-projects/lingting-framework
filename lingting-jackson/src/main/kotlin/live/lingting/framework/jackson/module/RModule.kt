package live.lingting.framework.jackson.module

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
import com.fasterxml.jackson.databind.module.SimpleDeserializers
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.TextNode
import live.lingting.framework.api.R

/**
 * @author lingting 2023-09-27 11:25
 */
class RModule : SimpleModule() {
    init {
        setDeserializers(RJacksonDeserializers())
    }

    class RJacksonDeserializers : SimpleDeserializers() {

        override fun findBeanDeserializer(
            type: JavaType, config: DeserializationConfig,
            beanDesc: BeanDescription
        ): JsonDeserializer<*>? {
            val rawClass = type.rawClass
            if (R::class.java.isAssignableFrom(rawClass)) {
                return RDeserializer(beanDesc)
            }
            return super.findBeanDeserializer(type, config, beanDesc)
        }
    }

    class RDeserializer(private val beanDesc: BeanDescription) : JsonDeserializer<R<*>>() {

        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): R<*> {
            val root = p.codec.readTree<TreeNode>(p)
            val code = getCode(root)
            val message = getMessage(root)
            val data = getData(root, getDefinition(FIELD_DATA)!!, ctxt)
            return R.of(code, data, message!!)
        }

        fun getCode(root: TreeNode): Int {
            val node = root[FIELD_CODE]
            if (isNull(node) || !node.isValueNode || node !is IntNode) {
                return -1
            }
            return node.asInt()
        }

        fun getMessage(root: TreeNode): String? {
            val node = root[FIELD_MESSAGE]
            if (isNull(node) || !node.isValueNode || node !is TextNode) {
                return null
            }
            return node.asText()
        }

        fun getData(root: TreeNode, definition: BeanPropertyDefinition, ctxt: DeserializationContext): Any? {
            val node = root[FIELD_DATA]
            if (isNull(node)) {
                return null
            }
            val javaType = definition.primaryType

            val deserializer = ctxt.findRootValueDeserializer(javaType)

            node.traverse().use { parser ->
                parser.nextToken()
                return deserializer.deserialize(parser, ctxt)
            }
        }

        fun getDefinition(field: String): BeanPropertyDefinition? {
            return beanDesc.findProperties()
                .stream()
                .filter { definition -> definition.name == field }
                .findFirst()
                .orElse(null)
        }

        fun isNull(node: TreeNode?): Boolean {
            return node == null || node is NullNode
        }
    }

    companion object {
        const val FIELD_CODE: String = "code"

        const val FIELD_DATA: String = "data"

        const val FIELD_MESSAGE: String = "message"
    }
}
