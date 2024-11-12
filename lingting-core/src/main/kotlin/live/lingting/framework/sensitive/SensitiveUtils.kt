package live.lingting.framework.sensitive

import live.lingting.framework.Sequence
import live.lingting.framework.sensitive.serializer.SensitiveDefaultProvider
import live.lingting.framework.util.StringUtils
import java.util.*

/**
 * @author lingting 2023-04-27 15:42
 */
class SensitiveUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        const val MIDDLE: String = "******"

        /**
         * 脱敏字符串序列化
         *
         * @param raw          原始字符串
         * @param prefixLength 结果前缀长度
         * @param suffixLength 结果后缀长度
         */

        fun serialize(raw: String, middle: String?, prefixLength: Int, suffixLength: Int): String {
            if (!StringUtils.hasText(raw)) {
                return ""
            }

            // 如果关闭脱敏
            if (!SensitiveHolder.allowSensitive()) {
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


        fun serialize(raw: String, prefixLength: Int, suffixLength: Int): String {
            return serialize(raw, MIDDLE, prefixLength, suffixLength)
        }


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

        fun providers(): List<SensitiveProvider> {
            val providers: MutableList<SensitiveProvider> = ArrayList()
            providers.add(SensitiveDefaultProvider.INSTANCE)

            try {
                val loader = ServiceLoader.load<SensitiveProvider>(SensitiveProvider::class.java)

                loader.stream().filter { obj: ServiceLoader.Provider<SensitiveProvider>? -> Objects.nonNull(obj) }.forEach { provider: ServiceLoader.Provider<SensitiveProvider> ->
                    try {
                        val p = provider.get()
                        if (p != null) {
                            providers.add(p)
                        }
                    } catch (error: ServiceConfigurationError) {
                        //
                    }
                }
            } catch (e: ServiceConfigurationError) {
                //
            }

            Sequence.asc<SensitiveProvider>(providers)
            return providers
        }
    }
}
