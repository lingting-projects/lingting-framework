package live.lingting.framework.api

import java.io.Serializable

/**
 * @author lingting 2024-01-25 11:12
 */

data class R<T>(val code: Int, val data: T?, val message: String) : Serializable {
    companion object {

        fun <T> of(code: Int, message: String): R<T> {
            return of<T>(code, message, null)
        }

        fun <T> of(code: Int, message: String, data: T?): R<T> {
            return R(code, data, message)
        }

        fun <T> ok(): R<T> {
            return ok(null)
        }

        fun <T> ok(data: T?): R<T> {
            return ok(ApiResultCode.SUCCESS, data)
        }

        fun <T> ok(code: ResultCode, data: T?): R<T> {
            return of(code.code, data, code.message)
        }

        fun <T> failed(code: ResultCode): R<T> {
            return of(code.code, code.message)
        }

        fun <T> failed(code: ResultCode, message: String): R<T> {
            return of(code.code, message)
        }

        fun <T> failed(code: Int, message: String): R<T> {
            return of(code, null, message)
        }

        fun <T> of(code: Int, data: T?, message: String): R<T> {
            return R(code, data, message)
        }
    }
}
