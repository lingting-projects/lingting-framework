package live.lingting.framework.huawei

import live.lingting.framework.aws.AwsS3Client
import live.lingting.framework.aws.s3.interfaces.AwsS3Delegation
import live.lingting.framework.huawei.obs.HuaweiObsS3Listener

/**
 * @author lingting 2024-09-13 13:45
 */
abstract class HuaweiObs<C : AwsS3Client> protected constructor(protected val client: C) : AwsS3Delegation<C> {

    companion object {

        const val HEADER_PREFIX: String = "x-obs"

        const val HEADER_PREFIX_META = "$HEADER_PREFIX-meta-"

        const val HEADER_TOKEN = "$HEADER_PREFIX-security-token"
    }

    init {
        client.listener = HuaweiObsS3Listener(client)
    }

    override fun delegation(): C {
        return client
    }
}
