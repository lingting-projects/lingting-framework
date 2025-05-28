package live.lingting.framework.http.body

import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.value.multi.StringMultiValue
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * @author lingting 2025/5/28 15:59
 */
class FormBodyBuilder : StringMultiValue() {

    @JvmOverloads
    fun build(
        encode: Boolean = true,
        charset: Charset = StandardCharsets.UTF_8,
        sort: Boolean = false
    ): MemoryBody {
        val query = HttpUrlBuilder.buildQuery(this, encode, charset, sort)
        return MemoryBody(query, charset)
    }

}
