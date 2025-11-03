package live.lingting.framework.ali

import live.lingting.framework.aws.AwsBasic
import live.lingting.framework.aws.AwsS3Bucket
import live.lingting.framework.aws.AwsS3Object
import live.lingting.framework.aws.AwsSts
import live.lingting.framework.aws.S3BasicTest
import live.lingting.framework.aws.properties.AwsS3Properties
import live.lingting.framework.aws.properties.S3Properties
import live.lingting.framework.aws.s3.interfaces.AwsS3BucketDelegation
import live.lingting.framework.aws.s3.interfaces.AwsS3ObjectDelegation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty

/**
 * @author lingting 2024-09-18 14:29
 */
@EnabledIfSystemProperty(named = "framework.aws.s3.test", matches = "true")
class AwsS3Test : S3BasicTest() {

    var sts: AwsSts? = null

    var useSts = false

    @BeforeEach
    fun before() {
        sts = AwsBasic.sts()
    }

    @Test
    fun test() {
        run()
    }

    override fun buildObj(key: String): AwsS3ObjectDelegation {
        val obj = if (useSts) sts!!.s3Object(properties as AwsS3Properties, key) else AwsS3Object(
            AwsBasic.s3Properties(),
            key
        )
        return object : AwsS3ObjectDelegation {
            override fun delegation(): AwsS3Object = obj
        }
    }

    override fun buildBucket(): AwsS3BucketDelegation {
        val bucket = if (useSts) sts!!.s3Bucket(properties as AwsS3Properties) else AwsS3Bucket(AwsBasic.s3Properties())
        return object : AwsS3BucketDelegation {
            override fun delegation(): AwsS3Bucket = bucket
        }
    }

    override fun properties(): S3Properties {
        return if (useSts) AwsBasic.s3StsProperties() else AwsBasic.s3Properties()
    }

}
