package live.lingting.framework.data

import live.lingting.framework.data.DataSizeUnit.BYTES
import live.lingting.framework.data.DataSizeUnit.GB
import live.lingting.framework.data.DataSizeUnit.MB
import live.lingting.framework.data.DataSizeUnit.TB
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.FileSystems
import java.nio.file.Files

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
            assertEquals(it.usableSpace - 1, (usable - BYTES.size).bytes)
            val unallocated = DataSize.ofBytes(it.unallocatedSpace)
            assertEquals(it.unallocatedSpace, unallocated.bytes)
            val used = DataSize.ofBytes(it.totalSpace - it.unallocatedSpace)
            assertEquals(it.totalSpace - it.unallocatedSpace, used.bytes)
            assertEquals(it.totalSpace - it.unallocatedSpace, (total - unallocated).bytes)
        }

        testValid("84.31 GB", 84, GB, 90527173181L)
        testValid("84.31GB", 84, GB, 90527173181L)
        testValid("123.45 MB", 123, MB, 129446707L)
        testValid("1.5TB", 1, TB, 1649267441664L)
        testValid("100", 100, BYTES, 100L)
        testValid("1 Bytes", 1, BYTES, 1L)
        testValid("1024KB", 1, MB, 1048576L)
        testValid("123.45GB extra", 123, GB, 132553428172L)

        testInvalid("invalid")
        testInvalid("123.45XB")
        testInvalid("")
        testInvalid("GB")
    }

    fun testValid(source: String, expectedValue: Long, expectedUnit: DataSizeUnit, expectedBytes: Long) {
        val size = DataSize.of(source)
        assertNotNull(size)
        assertEquals(expectedValue, size?.value)
        assertEquals(expectedUnit, size?.unit)
        assertEquals(expectedBytes, size?.bytes)
    }

    fun testInvalid(source: String) {
        assertNull(DataSize.of(source))
    }

}
