package live.lingting.framework.aws.s3.impl

import live.lingting.framework.http.header.CollectionHttpHeaders

/**
 * @author lingting 2025/6/30 17:07
 */
open class S3Meta(prefix: String) : CollectionHttpHeaders() {

    companion object {

        @JvmStatic
        fun empty() = S3Meta("")

    }

    val prefix = prefix.lowercase()

    override fun convert(key: String): String {
        val lowercase = key.lowercase()
        if (prefix.isBlank()) {
            return lowercase
        }
        return lowercase.substringAfter(prefix)
    }

}
