package live.lingting.framework.ali

import live.lingting.framework.ali.oss.AliOssS3Listener
import live.lingting.framework.aws.AwsS3Client
import live.lingting.framework.aws.s3.interfaces.AwsS3Delegation
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author lingting 2024-09-19 22:05
 */
abstract class AliOss<C : AwsS3Client?> protected constructor(protected val client: C) : AwsS3Delegation<C> {
    protected val log: Logger = LoggerFactory.getLogger(javaClass)

    init {
        client!!.listener = AliOssS3Listener(client)
    }

    override fun delegation(): C {
        return client
    }
}
