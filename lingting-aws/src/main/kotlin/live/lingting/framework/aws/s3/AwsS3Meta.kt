package live.lingting.framework.aws.s3

import live.lingting.framework.aws.AwsUtils
import live.lingting.framework.http.header.CollectionHttpHeaders
import live.lingting.framework.http.header.HttpHeaders

/**
 * @author lingting 2025/1/15 11:03
 */
open class AwsS3Meta @JvmOverloads constructor(
    prefix: String = AwsUtils.HEADER_PREFIX_META,
    source: HttpHeaders? = null,
) : CollectionHttpHeaders() {

    val prefix: String = prefix.lowercase()

    init {
        source?.run {
            from(source)
        }
    }

    override fun convert(key: String): String {
        val lowercase = key.lowercase()
        return lowercase.substringAfter(prefix)
    }

}
