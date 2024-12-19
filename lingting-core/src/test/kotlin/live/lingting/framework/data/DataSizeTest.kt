package live.lingting.framework.data

import java.nio.file.FileSystems
import java.nio.file.Files
import kotlin.test.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024/12/19 15:49
 */
class DataSizeTest {

    @Test
    fun test() {
        val stores = FileSystems.getDefault().rootDirectories.map { Files.getFileStore(it) }
        stores.forEach {
            assertNotNull(it)
            assertTrue { it.totalSpace > 0 }
            val total = DataSize.ofBytes(it.totalSpace)
            assertEquals(it.totalSpace, total.bytes)
            assertNotNull(total.scaleValue)
            val usable = DataSize.ofBytes(it.usableSpace)
            assertEquals(it.usableSpace - 1, (usable - DataSizeUnit.BYTES.size).bytes)
            val unallocated = DataSize.ofBytes(it.unallocatedSpace)
            assertEquals(it.unallocatedSpace, unallocated.bytes)
            val used = DataSize.ofBytes(it.totalSpace - it.unallocatedSpace)
            assertEquals(it.totalSpace - it.unallocatedSpace, used.bytes)
            assertEquals(it.totalSpace - it.unallocatedSpace, (total - unallocated).bytes)
        }
    }
}
