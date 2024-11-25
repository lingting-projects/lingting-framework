package live.lingting.framework.mybatis.methods

import org.apache.ibatis.executor.keygen.KeyGenerator

/**
 * @author lingting 2023-12-21 21:21
 */
data class KeyResult(
    val generator: KeyGenerator?,

    val column: String?,

    val property: String?,
)
