package live.lingting.framework.grpc

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024/11/20 14:31
 */
class GrpcServerTest {

    @Test
    fun test() {
        val server = GrpcServerBuilder().port(0).build()
        server.onApplicationStart()
        assertTrue(server.port() != 0)
    }

}
