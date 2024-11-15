package live.lingting.framework.api

import java.io.Serializable

/**
 * @author lingting 2024-01-25 11:12
 */

data class R<T>(val code: Int, val data: T?, val message: String) : Serializable {
    companion object {

        @JvmStatic
        fun <T> of(code: Int, message: String): R<T> {
            return of<T>(code, message, null)
        }

        @JvmStatic
        fun <T> of(code: Int, message: String, data: T?): R<T> {
            return R(code, data, message)
        }

        @JvmStatic
        fun <T> ok(): R<T> {
            return ok(null)
        }

        @JvmStatic
        fun <T> ok(data: T?): R<T> {
            return ok(ApiResultCode.SUCCESS, data)
        }

        @JvmStatic
        fun <T> ok(code: ResultCode, data: T?): R<T> {
            return of(code.code, data, code.message)
        }

        @JvmStatic
        fun <T> failed(code: ResultCode): R<T> {
            return of(code.code, code.message)
        }

        @JvmStatic
        fun <T> failed(code: ResultCode, message: String): R<T> {
            return of(code.code, message)
        }

        @JvmStatic
        fun <T> failed(code: Int, message: String): R<T> {
            return of(code, null, message)
        }

        @JvmStatic
        fun <T> of(code: Int, data: T?, message: String): R<T> {
            return R(code, data, message)
        }

    }
}
