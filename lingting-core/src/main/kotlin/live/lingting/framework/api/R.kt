package live.lingting.framework.api

import java.io.Serializable

/**
 * @author lingting 2024-01-25 11:12
 */

data class R<T>(val code: Int, val data: T?, val message: String) : Serializable {
    companion object {

        @JvmStatic
        @JvmOverloads
        fun <T> of(code: Int, message: String, data: T? = null): R<T> {
            return R(code, data, message)
        }

        @JvmStatic
        @JvmOverloads
        fun <T> of(rc: ResultCode, data: T? = null): R<T> {
            return of(rc.code, rc.i18nMessage(), data)
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
            return of(code, data)
        }

        @JvmStatic
        fun <T> failed(code: ResultCode): R<T> {
            return of(code)
        }

        @JvmStatic
        fun <T> failed(code: ResultCode, message: String): R<T> {
            return of(code.code, message)
        }

        @JvmStatic
        fun <T> failed(code: Int, message: String): R<T> {
            return of(code, message)
        }


    }
}
