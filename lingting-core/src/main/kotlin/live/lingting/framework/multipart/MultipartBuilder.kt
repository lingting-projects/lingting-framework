package live.lingting.framework.multipart

import live.lingting.framework.data.DataSize
import live.lingting.framework.stream.FileCloneInputStream
import live.lingting.framework.util.DataSizeUtils.bytes
import live.lingting.framework.util.ValueUtils
import java.io.File
import java.io.InputStream

/**
 * @author lingting 2024-09-14 10:39
 */
class MultipartBuilder {

    var partSize: DataSize = DataSize.ZERO
        private set

    var partDir: File = Multipart.TEMP_DIR
        private set

    var id: String = ValueUtils.simpleUuid()
        private set

    var source: FileCloneInputStream? = null
        private set

    var size: DataSize = DataSize.ZERO
        private set

    var maxPartSize: DataSize = DataSize.ZERO
        private set

    var minPartSize: DataSize = DataSize.ZERO
        private set

    var maxPartCount: Long = 0
        private set

    fun partSize(partSize: DataSize): MultipartBuilder {
        this.partSize = partSize
        return this
    }

    fun partDir(partDir: File): MultipartBuilder {
        this.partDir = partDir
        return this
    }

    fun id(id: String): MultipartBuilder {
        this.id = id
        return this
    }

    fun source(source: InputStream): MultipartBuilder {
        this.source = source as? FileCloneInputStream ?: FileCloneInputStream(source)
        return size(this.source!!.size().bytes)
    }

    fun source(file: File): MultipartBuilder {
        return source(FileCloneInputStream(file))
    }

    fun size(size: DataSize): MultipartBuilder {
        this.size = size
        return this
    }

    fun maxPartSize(maxPartSize: DataSize): MultipartBuilder {
        this.maxPartSize = maxPartSize
        return this
    }

    fun minPartSize(minPartSize: DataSize): MultipartBuilder {
        this.minPartSize = minPartSize
        return this
    }

    fun maxPartCount(maxPartCount: Long): MultipartBuilder {
        this.maxPartCount = maxPartCount
        return this
    }

    fun parts(): Collection<Part> {
        if (minPartSize.bytes > 0 && partSize < minPartSize) {
            partSize(minPartSize)
        }
        val number = Multipart.calculate(size, partSize)
        // 限制了最大分片数量. 超过之后重新分配每片大小
        if (maxPartCount > 0 && number > maxPartCount) {
            partSize(partSize + (partSize / 2))
            return parts()
        }
        // 限制了分片最大大小, 超时后需要缩减
        if (maxPartSize.bytes > 0 && partSize > maxPartSize) {
            if (size < minPartSize) {
                partSize(minPartSize)
            } else if (maxPartCount > 0) {
                partSize((partSize / maxPartCount).bytes)
                // 如果依旧超出, 则表示无法满足要求
                if (partSize > maxPartSize) {
                    throw IllegalArgumentException("无法计算分片. 原始大小: $size, 最大分片数: $maxPartCount, 单分片最小大小: $minPartSize, 单分票最大大小: $maxPartSize")
                }
            } else {
                partSize(partSize - (partSize / 2))
            }
            return parts()
        }
        return Multipart.split(size, partSize)
    }

    fun build(): Multipart {
        val parts = parts()
        return Multipart(id, source?.source(), size, partSize, parts, partDir)
    }

}
