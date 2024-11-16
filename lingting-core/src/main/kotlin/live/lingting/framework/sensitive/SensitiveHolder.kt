package live.lingting.framework.sensitive

/**
 * @author lingting 2023-06-30 17:57
 */
object SensitiveHolder {

    private val THREAD_LOCAL = ThreadLocal<Boolean>()

    /**
     * @return 是否允许对敏感信息进行脱敏. 默认值为true
     */
    @JvmStatic
    fun allowSensitive(): Boolean {
        return THREAD_LOCAL.get() ?: true
    }

    /**
     * 是否允许对敏感信息进行脱敏
     */
    @JvmStatic
    fun setSensitive(flag: Boolean) {
        THREAD_LOCAL.set(flag)
    }

    @JvmStatic
    fun remove() {
        THREAD_LOCAL.remove()
    }

}
