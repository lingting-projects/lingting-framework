package live.lingting.framework.download

import live.lingting.framework.util.FileUtils
import java.io.File

/**
 * @author lingting 2024-01-17 10:09
 */
interface Download {
    fun start(): Download

    fun await(): Download

    val isStart: Boolean

    val isFinished: Boolean

    val isSuccess: Boolean

    val file: File

    /**
     * 使用下载文件覆盖指定文件
     */

    fun transferTo(file: File): File {
        val source = this.file
        FileUtils.copy(source, file, true)
        return file
    }
}
