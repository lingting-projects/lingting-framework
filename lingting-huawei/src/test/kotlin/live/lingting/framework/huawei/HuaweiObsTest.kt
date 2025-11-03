package live.lingting.framework.huawei

import live.lingting.framework.aws.S3BasicTest
import live.lingting.framework.aws.properties.S3Properties
import live.lingting.framework.aws.s3.interfaces.AwsS3BucketDelegation
import live.lingting.framework.aws.s3.interfaces.AwsS3ObjectDelegation
import live.lingting.framework.huawei.properties.HuaweiObsProperties
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty

/**
 * @author lingting 2024-09-13 17:13
 */
@EnabledIfSystemProperty(named = "framework.huawei.obs.test", matches = "true")
class HuaweiObsTest : S3BasicTest() {

    var iam: HuaweiIam? = null

    @BeforeEach
    fun before() {
        iam = HuaweiBasic.iam()
    }

    @Test
    fun test() {
        run()
    }

    override fun buildObj(key: String): AwsS3ObjectDelegation = iam!!.obsObject(properties as HuaweiObsProperties, key)

    override fun buildBucket(): AwsS3BucketDelegation = iam!!.obsBucket(properties as HuaweiObsProperties)

    override fun properties(): S3Properties = HuaweiBasic.obsProperties()

}
