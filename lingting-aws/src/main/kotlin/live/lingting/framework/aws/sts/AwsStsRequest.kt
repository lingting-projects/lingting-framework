package live.lingting.framework.aws.sts

import live.lingting.framework.aws.AwsRequest

/**
 * @author lingting 2025/6/3 14:47
 */
abstract class AwsStsRequest : AwsRequest() {

    override fun version(): String = "2011-06-15"

}
