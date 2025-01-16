package live.lingting.framework.http

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.CookieStore
import java.net.ProxySelector
import java.net.URI
import java.time.Duration
import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Consumer
import javax.net.SocketFactory
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager
import live.lingting.framework.stream.BytesInputStream
import live.lingting.framework.stream.FileCloneInputStream
import live.lingting.framework.util.FileUtils
import live.lingting.framework.util.StreamUtils
import live.lingting.framework.util.ThreadUtils
import live.lingting.framework.value.LazyValue

/**
 * @author lingting 2024-09-02 15:28
 */
@Suppress("UNCHECKED_CAST")
abstract class HttpClient {

    companion object {
        /**
         * @see jdk.internal.net.http.common.Utils.getDisallowedHeaders
         */
        @JvmField
        val HEADERS_DISABLED: Set<String> = setOf(
            "connection", "content-length", "expect", "host",
            "upgrade"
        )

        @JvmField
        val TEMP_DIR: File = FileUtils.createTempDir("http")

        /**
         * 默认大小以内文件直接放内存里面, 单位: bytes
         */
        @JvmField
        var defaultMaxBytes: Long = 1048576

        @JvmStatic
        fun builder() = default()

        fun default() = okhttp()

        @JvmStatic
        fun java(): JavaHttpClient.Builder {
            return JavaHttpClient.Builder()
        }

        @JvmStatic
        fun okhttp(): OkHttpClient.Builder {
            return OkHttpClient.Builder()
        }

        @JvmStatic
        @JvmOverloads
        fun wrap(source: InputStream?, maxBytes: Long = defaultMaxBytes): InputStream {
            if (source == null) {
                return ByteArrayInputStream(ByteArray(0))
            }
            val size = AtomicLong(0)

            val byteOut = ByteArrayOutputStream()
            val fileOutValue = LazyValue<FileOutputStream?> { null }
            val fileValue = LazyValue<File> {
                val file = FileUtils.createTemp(".wrap", TEMP_DIR)
                fileOutValue.set(FileOutputStream(file))
                file
            }
            StreamUtils.read(source) { bytes, len ->
                if (!fileValue.isFirst() || size.addAndGet(len.toLong()) > maxBytes) {
                    if (fileValue.isFirst()) {
                        fileValue.get()
                        fileOutValue.get()!!.write(byteOut.toByteArray())
                    }
                    fileOutValue.get()!!.write(bytes, 0, len)
                } else {
                    byteOut.write(bytes, 0, len)
                }
            }
            if (fileValue.isFirst()) {
                return BytesInputStream(byteOut.toByteArray())
            }
            return FileCloneInputStream(fileValue.get()!!)
        }
    }

    protected var cookie: CookieStore? = null

    fun cookie(): CookieStore? {
        return cookie
    }

    abstract fun client(): Any

    abstract fun request(request: HttpRequest): HttpResponse

    abstract fun request(request: HttpRequest, callback: ResponseCallback)

    fun <T> request(request: HttpRequest, cls: Class<T>): T {
        val response = request(request)
        return response.convert(cls)
    }

    fun get(uri: URI): HttpResponse {
        return request(HttpRequest.builder().get().url(uri).build())
    }

    abstract class Builder<C : HttpClient, B : Builder<C, B>> {
        protected var executor: ExecutorService? = ThreadUtils.executor()

        protected var redirects: Boolean = true

        protected var socketFactory: SocketFactory? = null

        /**
         * HostnameVerifier，用于HTTPS安全连接
         */
        protected var hostnameVerifier: HostnameVerifier? = null

        /**
         * 用于HTTPS安全连接
         */
        protected var sslContext: SSLContext? = null

        protected var trustManager: X509TrustManager? = null

        /**
         * java 实现无效
         */
        protected var callTimeout: Duration? = null

        protected var connectTimeout: Duration? = null

        protected var readTimeout: Duration? = null

        protected var writeTimeout: Duration? = null

        protected var proxySelector: ProxySelector? = null

        protected var cookie: CookieStore? = null

        fun socketFactory(socketFactory: SocketFactory?): B {
            this.socketFactory = socketFactory
            return this as B
        }

        fun executor(executor: ExecutorService): B {
            this.executor = executor
            return this as B
        }

        fun redirects(redirects: Boolean): B {
            this.redirects = redirects
            return this as B
        }

        fun hostnameVerifier(hostnameVerifier: HostnameVerifier?): B {
            this.hostnameVerifier = hostnameVerifier
            return this as B
        }

        fun ssl(trustManager: X509TrustManager): B {
            val context = Https.sslContext(trustManager)
            return ssl(context, trustManager)
        }

        fun ssl(context: SSLContext?, trustManager: X509TrustManager?): B {
            this.sslContext = context
            this.trustManager = trustManager
            return this as B
        }

        fun disableSsl(): B {
            val manager = Https.SSL_DISABLED_TRUST_MANAGER
            val verifier = Https.SSL_DISABLED_HOSTNAME_VERIFIER
            return ssl(manager).hostnameVerifier(verifier)
        }


        /**
         * java 实现无效
         */
        fun callTimeout(callTimeout: Duration?): B {
            this.callTimeout = callTimeout
            return this as B
        }

        fun connectTimeout(connectTimeout: Duration?): B {
            this.connectTimeout = connectTimeout
            return this as B
        }

        fun readTimeout(readTimeout: Duration?): B {
            this.readTimeout = readTimeout
            return this as B
        }

        fun writeTimeout(writeTimeout: Duration?): B {
            this.writeTimeout = writeTimeout
            return this as B
        }

        /**
         * 无限等待时间
         */
        open fun infiniteTimeout(): B {
            return timeout(Duration.ZERO, Duration.ZERO, Duration.ZERO, Duration.ZERO)
        }

        fun timeout(connectTimeout: Duration?, readTimeout: Duration?): B {
            return connectTimeout(connectTimeout).readTimeout(readTimeout)
        }

        fun timeout(callTimeout: Duration?, connectTimeout: Duration?, readTimeout: Duration?, writeTimeout: Duration?): B {
            return callTimeout(callTimeout).connectTimeout(connectTimeout)
                .readTimeout(readTimeout)
                .writeTimeout(writeTimeout)
        }

        fun proxySelector(proxySelector: ProxySelector?): B {
            this.proxySelector = proxySelector
            return this as B
        }

        fun cookie(cookie: CookieStore?): B {
            this.cookie = cookie
            return this as B
        }

        fun memoryCookie(): B {
            val manager = CookieManager(null, CookiePolicy.ACCEPT_ALL)
            return cookie(manager.cookieStore)
        }

        protected fun <A> nonNull(a: A?, consumer: Consumer<A>) {
            if (a == null) {
                return
            }
            consumer.accept(a)
        }

        fun build(): C {
            val c = doBuild()
            c.cookie = cookie
            return c
        }

        protected abstract fun doBuild(): C
    }

}
