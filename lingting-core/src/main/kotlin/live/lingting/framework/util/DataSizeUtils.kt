package live.lingting.framework.util

import live.lingting.framework.data.DataSizeUnit

/**
 * @author lingting 2025/4/28 11:38
 */
object DataSizeUtils {

    @JvmStatic
    inline val Number.bytes get() = DataSizeUnit.BYTES.of(toLong())

    @JvmStatic
    inline val Number.kb get() = DataSizeUnit.KB.of(toLong())

    @JvmStatic
    inline val Number.mb get() = DataSizeUnit.MB.of(toLong())

    @JvmStatic
    inline val Number.gb get() = DataSizeUnit.GB.of(toLong())

    @JvmStatic
    inline val Number.tb get() = DataSizeUnit.TB.of(toLong())

    @JvmStatic
    inline val Number.pb get() = DataSizeUnit.PB.of(toLong())

}
