package live.lingting.framework.security.grpc.authorization

import live.lingting.framework.security.password.SecurityPassword

/**
 * @author lingting 2024-01-30 20:35
 */
class Password : SecurityPassword {
    override fun encodeFront(plaintext: String): String {
        return plaintext
    }

    override fun decodeFront(ciphertext: String): String {
        return ciphertext
    }

    override fun encode(plaintext: String): String {
        return plaintext
    }

    override fun match(plaintext: String, ciphertext: String): Boolean {
        return true
    }
}
