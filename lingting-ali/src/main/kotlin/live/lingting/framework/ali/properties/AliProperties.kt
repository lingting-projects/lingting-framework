package live.lingting.framework.ali.properties

/**
 * @author lingting 2024-09-14 14:16
 */
abstract class AliProperties {

    companion object {

        @JvmField
        val ENDPOINT = "aliyuncs.com"

    }

    open var ssl: Boolean = true

    open var region: String = "us-east-1"

    open var endpoint: String = ENDPOINT

    open var ak: String = ""

    open var sk: String = ""

    open var token: String? = ""

    abstract fun host(): String

}
