package live.lingting.framework.http.api

import com.fasterxml.jackson.annotation.JsonIgnore
import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.body.BodySource
import live.lingting.framework.http.body.MemoryBody
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.jackson.JacksonUtils
import live.lingting.framework.value.multi.StringMultiValue

/**
 * @author lingting 2024-09-14 15:33
 */
abstract class ApiRequest {

    @JsonIgnore
    val headers: HttpHeaders = HttpHeaders.empty()

    @JsonIgnore
    val params: StringMultiValue = StringMultiValue()

    abstract fun method(): HttpMethod

    abstract fun path(): String

    open fun body(): BodySource {
        val json = JacksonUtils.toJson(this)
        return MemoryBody(json)
    }

    /**
     * 在发起请求前触发
     */
    open fun onCall() {
        //
    }

    open fun onParams() {
        //
    }
}
