package live.lingting.framework.stream

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Files
import live.lingting.framework.util.FileUtils

/**
 * 克隆输入流, 可直接读取, 也可以克隆出一个新流然后读取
 *
 *
 * 当直接读取时, 所有行为和文件流一致
 *
 *
 * @author lingting 2024-01-09 15:41
 */
class FileCloneInputStream constructor(source: File, size: Long = Files.size(source.toPath())) : CloneInputStream(source, size) {
    constructor(input: FileCloneInputStream) : this(input.source(), input.size())

    constructor(input: InputStream) : this(
        if (input is FileCloneInputStream)
            input.source()
        else
            FileUtils.createTemp(input, ".clone", TEMP_DIR)
    )


    override fun newStream(): InputStream {
        return FileInputStream(source())
    }


    override fun copy(): FileCloneInputStream {
        return FileCloneInputStream(this)
    }

    override fun source(): File {
        return source as File
    }

    override fun clear() {
        FileUtils.delete(source())
    }
}
