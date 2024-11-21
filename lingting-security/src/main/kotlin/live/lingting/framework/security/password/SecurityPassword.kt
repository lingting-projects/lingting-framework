package live.lingting.framework.security.password

import live.lingting.framework.util.StringUtils

/**
 * @author lingting 2023-03-30 15:10
 */
interface SecurityPassword {
    fun valid(plaintext: String?): Boolean {
        return StringUtils.hasText(plaintext)
    }

    /**
     * 依据前端加密方式, 明文转密文
     */
    fun encodeFront(plaintext: String): String

    /**
     * 解析收到的前端密文
     */
    fun decodeFront(ciphertext: String): String

    /**
     * 密码明文转数据库存储的密文
     */
    fun encode(plaintext: String): String

    /**
     * 明文和密文是否匹配
     */
    fun match(plaintext: String, ciphertext: String): Boolean
}
