package live.lingting.framework.sensitive

import live.lingting.framework.Sequence
import live.lingting.framework.sensitive.serializer.SensitiveDefaultProvider
import live.lingting.framework.util.StringUtils
import java.util.ServiceConfigurationError
import java.util.ServiceLoader

/**
 * @author lingting 2023-04-27 15:42
 */
object SensitiveUtils {

    const val MIDDLE: String = "******"

    /**
     * 脱敏字符串序列化
     * @param raw          原始字符串
     * @param prefixLength 结果前缀长度
     * @param suffixLength 结果后缀长度
     */
    @JvmStatic
    fun serialize(raw: String, middle: String, prefixLength: Int, suffixLength: Int): String {
        if (!StringUtils.hasText(raw)) {
            return ""
        }

        // 如果关闭脱敏
        if (!SensitiveHolder.allow()) {
            return raw
        }

        val builder = StringBuilder()

        // 开头
        builder.append(raw, 0, prefixLength)

        // 中间
        if (raw.length > prefixLength) {
            builder.append(middle)
        }

        // 有没有结尾
        if (raw.length > prefixLength + suffixLength) {
            builder.append(raw, raw.length - suffixLength, raw.length)
        }

        return builder.toString()
    }

    @JvmStatic
    fun serialize(raw: String, prefixLength: Int, suffixLength: Int): String {
        return serialize(raw, MIDDLE, prefixLength, suffixLength)
    }

    @JvmStatic
    fun serialize(raw: String, prefixLength: Int, suffixLength: Int, sensitive: Sensitive?): String {
        var prefixLength = prefixLength
        var suffixLength = suffixLength
        if (sensitive != null) {
            if (sensitive.prefixLength > -1) {
                prefixLength = sensitive.prefixLength
            }
            if (sensitive.suffixLength > -1) {
                suffixLength = sensitive.suffixLength
            }

            if (StringUtils.hasText(sensitive.middle)) {
                return serialize(raw, sensitive.middle, prefixLength, suffixLength)
            }
        }
        return serialize(raw, prefixLength, suffixLength)
    }

    @JvmStatic
    fun findSerializer(sensitive: Sensitive): SensitiveSerializer? {
        val providers = providers()
        for (provider in providers) {
            val serializer = provider.find(sensitive)
            if (serializer != null) {
                return serializer
            }
        }
        return null
    }

    @JvmStatic
    fun providers(): List<SensitiveProvider> {
        val providers: MutableList<SensitiveProvider> = ArrayList()
        providers.add(SensitiveDefaultProvider)

        try {
            val loader = ServiceLoader.load(SensitiveProvider::class.java)

            loader.forEach {
                if (it == null) {
                    return@forEach
                }
                providers.add(it)
            }
        } catch (_: ServiceConfigurationError) {
            //
        }

        Sequence.asc<SensitiveProvider>(providers)
        return providers
    }

}
