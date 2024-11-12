package live.lingting.framework.crypto.mac

/**
 * @author lingting 2024-09-04 13:42
 */
class MacBuilder : AbstractMacBuilder<MacBuilder>() {
    class Hmac : AbstractMacBuilder<Hmac>() {
        fun sha256(): Hmac {
            return algorithm("HmacSHA256")
        }

        fun sha1(): Hmac {
            return algorithm("HmacSHA1")
        }
    }
}
