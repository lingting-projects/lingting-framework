package live.lingting.framework.aws.properties

/**
 * @author lingting 2025/6/3 15:43
 */
abstract class AwsProperties {

    companion object {

        const val REGION = "us-east-1"

    }

    open var ssl: Boolean = true

    open var region = REGION

    open var endpoint: String = "amazonaws.com"

    open var ak: String = ""

    open var sk: String = ""

    open var token: String? = ""

    abstract fun host(): String

}
