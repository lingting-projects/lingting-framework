package live.lingting.framework.jackson.provider

import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.cfg.CacheProvider
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider
import com.fasterxml.jackson.databind.ser.SerializerFactory
import live.lingting.framework.jackson.serializer.NullArrayJsonSerializer
import live.lingting.framework.jackson.serializer.NullMapJsonSerializer
import live.lingting.framework.jackson.serializer.NullStringJsonSerializer

/**
 * @author lingting
 */
class NullSerializerProvider : DefaultSerializerProvider {
    constructor() : super()

    constructor(src: NullSerializerProvider) : super(src)

    protected constructor(src: SerializerProvider, config: SerializationConfig, f: SerializerFactory?) : super(src, config, f)

    protected constructor(provider: NullSerializerProvider, cacheProvider: CacheProvider) : super(provider, cacheProvider)

    override fun copy(): DefaultSerializerProvider {
        if (javaClass != NullSerializerProvider::class.java) {
            return super.copy()
        }
        return NullSerializerProvider(this)
    }

    override fun withCaches(cacheProvider: CacheProvider): DefaultSerializerProvider {
        return NullSerializerProvider(this, cacheProvider)
    }

    override fun createInstance(config: SerializationConfig, jsf: SerializerFactory): NullSerializerProvider {
        return NullSerializerProvider(this, config, jsf)
    }


    override fun findNullValueSerializer(property: BeanProperty): JsonSerializer<Any> {
        val propertyType = property.type
        return if (isStringType(propertyType)) {
            nullStringJsonSerializer
        } else if (isArrayOrCollectionTrype(propertyType)) {
            nullArrayJsonSerializer
        } else if (isMapType(propertyType)) {
            nullMapJsonSerializer
        } else {
            super.findNullValueSerializer(property)
        }
    }

    /**
     * 是否是 String 类型
     * @param type JavaType
     * @return boolean
     */
    private fun isStringType(type: JavaType): Boolean {
        val clazz = type.rawClass
        return String::class.java.isAssignableFrom(clazz)
    }

    /**
     * 是否是Map类型
     * @param type JavaType
     * @return boolean
     */
    private fun isMapType(type: JavaType): Boolean {
        val clazz = type.rawClass
        return MutableMap::class.java.isAssignableFrom(clazz)
    }

    /**
     * 是否是集合类型或者数组
     * @param type JavaType
     * @return boolean
     */
    private fun isArrayOrCollectionTrype(type: JavaType): Boolean {
        val clazz = type.rawClass
        return clazz.isArray || MutableCollection::class.java.isAssignableFrom(clazz)
    }

    companion object {

        /**
         * null array 或 list，set 则转 '[]'
         */
        @JvmStatic
        val nullArrayJsonSerializer = NullArrayJsonSerializer()

        /**
         * null Map 转 '{}'
         */
        @JvmStatic
        val nullMapJsonSerializer = NullMapJsonSerializer()

        /**
         * null 字符串转 ''
         */
        @JvmStatic
        val nullStringJsonSerializer = NullStringJsonSerializer()
    }
}
