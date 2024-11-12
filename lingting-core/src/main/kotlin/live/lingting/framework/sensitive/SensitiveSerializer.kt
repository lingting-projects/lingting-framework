package live.lingting.framework.sensitive

/**
 * @author lingting 2024-01-26 17:56
 */
interface SensitiveSerializer {
    /**
     * 依据注解脱敏原始值
     */

    fun serialize(sensitive: Sensitive, raw: String): String
}
