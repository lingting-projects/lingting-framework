package live.lingting.framework.sensitive

import live.lingting.framework.context.Context

/**
 * @author lingting 2023-06-30 17:57
 */
object SensitiveHolder {

    private val CONTEXT = Context<Boolean>()

    /**
     * @return 是否允许对敏感信息进行脱敏. 默认值为false, 不对敏感信息进行脱敏
     */
    @JvmStatic
    fun allow(): Boolean {
        return CONTEXT.get() == true
    }

    /**
     * 是否允许对敏感信息进行脱敏
     */
    @JvmStatic
    fun set(flag: Boolean) {
        CONTEXT.set(flag)
    }

    @JvmStatic
    fun remove() {
        CONTEXT.remove()
    }

}
