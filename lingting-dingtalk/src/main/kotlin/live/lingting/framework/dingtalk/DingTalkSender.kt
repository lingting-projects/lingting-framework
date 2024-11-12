package live.lingting.framework.dingtalk

import live.lingting.framework.crypto.mac.Mac
import live.lingting.framework.dingtalk.message.DingTalkMessage
import live.lingting.framework.http.HttpClient
import live.lingting.framework.http.HttpClient.Companion.okhttp
import live.lingting.framework.http.HttpRequest.Companion.builder
import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.util.StringUtils
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.function.Supplier

/**
 * 订单消息发送
 *
 * @author lingting 2020/6/10 21:25
 */
class DingTalkSender(
    /**
     * 请求路径
     */
    @JvmField val url: String
) {
    /**
     * 密钥
     */
    var secret: String? = null
        protected set

    var mac: Mac? = null
        protected set

    var currentTimeMillisSupplier: Supplier<Long> = Supplier { System.currentTimeMillis() }
        private set

    /**
     * 发送消息 根据参数值判断使用哪种发送方式
     */
    fun sendMessage(message: DingTalkMessage): DingTalkResponse {
        return if (StringUtils.hasText(secret)) {
            sendSecretMessage(message)
        } else {
            sendNormalMessage(message)
        }
    }

    /**
     * 未使用 加签 安全设置 直接发送
     */
    fun sendNormalMessage(message: DingTalkMessage): DingTalkResponse {
        return request(message, false)
    }

    /**
     * 使用 加签 安全设置 发送
     */
    fun sendSecretMessage(message: DingTalkMessage): DingTalkResponse {
        return request(message, true)
    }

    /**
     * 设置密钥
     */
    fun setSecret(secret: String?): DingTalkSender {
        if (StringUtils.hasText(secret)) {
            this.secret = secret
            this.mac = Mac.hmacBuilder().sha256().secret(secret!!).build()
        } else {
            this.secret = null
            this.mac = null
        }
        return this
    }

    /**
     * 获取签名后的请求路径
     *
     * @param timestamp 当前时间戳
     */
    fun secret(timestamp: Long): String {
        val source = """
             $timestamp
             $secret
             """.trimIndent()
        val base64 = mac!!.calculateBase64(source)
        val sign = URLEncoder.encode(base64, StandardCharsets.UTF_8)
        return "%s&timestamp=%d&sign=%s".formatted(url, timestamp, sign)
    }

    /**
     * 发起消息请求
     *
     * @param dingTalkMessage 消息内容
     * @param isSecret        是否签名 true 签名
     * @return java.lang.String
     */
    fun request(dingTalkMessage: DingTalkMessage, isSecret: Boolean): DingTalkResponse {
        val message = dingTalkMessage.generate()
        val timestamp = currentTimeMillisSupplier.get()

        val requestUrl = if (isSecret) secret(timestamp) else url

        val urlBuilder = HttpUrlBuilder.from(requestUrl)
        val builder = builder()
            .post()
            .url(urlBuilder)
            .header("Content-Type", "application/json")
            .body(message!!)

        val request = builder.build()
        val json = CLIENT.request(request, String::class.java)
        return DingTalkResponse.Companion.of(json)
    }

    fun setCurrentTimeMillisSupplier(currentTimeMillisSupplier: Supplier<Long>): DingTalkSender {
        this.currentTimeMillisSupplier = currentTimeMillisSupplier
        return this
    }

    companion object {
        protected val CLIENT: HttpClient = okhttp()
            .timeout(Duration.ofSeconds(10), Duration.ofSeconds(10))
            .build()
    }
}
