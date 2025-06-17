package live.lingting.framework.dingtalk

import live.lingting.framework.crypto.mac.Mac
import live.lingting.framework.dingtalk.message.DingTalkMessage
import live.lingting.framework.http.HttpContentTypes
import live.lingting.framework.http.HttpRequest
import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.api.ApiClient
import live.lingting.framework.time.DateTime
import live.lingting.framework.util.StringUtils
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.function.Supplier

/**
 * 订单消息发送
 * @author lingting 2020/6/10 21:25
 */
class DingTalkSender(
    /**
     * 请求路径
     */
    @JvmField val url: String
) {
    var client = ApiClient.defaultClient

    /**
     * 密钥
     */
    var secret: String? = null
        private set

    var mac: Mac? = null
        private set

    var currentTimeMillisSupplier: Supplier<Long> = Supplier { DateTime.millis() }

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
    fun useSecret(secret: String): DingTalkSender {
        if (StringUtils.hasText(secret)) {
            this.secret = secret
            this.mac = Mac.hmacBuilder().sha256().secret(secret).build()
        } else {
            this.secret = null
            this.mac = null
        }
        return this
    }

    /**
     * 获取签名后的请求路径
     * @param timestamp 当前时间戳
     */
    fun secret(timestamp: Long): String {
        val source = "$timestamp\n$secret"
        val base64 = mac!!.calculateBase64(source)
        val sign = URLEncoder.encode(base64, StandardCharsets.UTF_8.name())
        return "$url&timestamp=$timestamp&sign=$sign"
    }

    /**
     * 发起消息请求
     * @param dingTalkMessage 消息内容
     * @param isSecret        是否签名 true 签名
     * @return java.lang.String
     */
    fun request(dingTalkMessage: DingTalkMessage, isSecret: Boolean): DingTalkResponse {
        val message = dingTalkMessage.generate()
        val timestamp = currentTimeMillisSupplier.get()

        val requestUrl = if (isSecret) secret(timestamp) else url

        val urlBuilder = HttpUrlBuilder.from(requestUrl)
        val builder = HttpRequest.builder()
            .post()
            .url(urlBuilder)
            .header("Content-Type", HttpContentTypes.JSON)
            .body(message)

        val request = builder.build()
        val json = client.request(request, String::class.java)
        return DingTalkResponse.of(json)
    }

}
