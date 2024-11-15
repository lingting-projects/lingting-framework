package live.lingting.framework.crypto.cipher

/**
 * @author lingting 2024-09-04 11:26
 */
@Suppress("Unchecked", "kotlin:S6530", "UNCHECKED_CAST")
class CipherBuilder : AbstractCipherBuilder<CipherBuilder>() {
    abstract class SpecificCipherBuilder<B : SpecificCipherBuilder<B>>
    protected constructor(algorithm: String) : AbstractCipherBuilder<B>() {
        init {
            this.algorithm = algorithm
        }

        override fun algorithm(algorithm: String): B {
            return this as B
        }
    }

    class AES : SpecificCipherBuilder<AES>("AES") {
        fun ecb(): AES {
            return mode("ECB")
        }

        fun cbc(): AES {
            return mode("CBC")
        }

        fun ctr(): AES {
            return mode("CTR")
        }

        fun ofb(): AES {
            return mode("OFB")
        }

        fun cfb(): AES {
            return mode("CFB")
        }

        fun pkcs5(): AES {
            return padding("PKCS5Padding")
        }

        fun pkcs7(): AES {
            return padding("PKCS5Padding")
        }

        fun iso10126(): AES {
            return padding("ISO10126Padding")
        }

        fun no(): AES {
            return padding("NoPadding")
        }
    }
}
