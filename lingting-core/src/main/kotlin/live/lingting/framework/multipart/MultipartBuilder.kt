package live.lingting.framework.multipart

import java.io.File
import java.io.InputStream
import live.lingting.framework.stream.FileCloneInputStream
import live.lingting.framework.util.ValueUtils

/**
 * @author lingting 2024-09-14 10:39
 */
class MultipartBuilder {
    var partSize: Long = 0
        private set

    var id: String = ValueUtils.simpleUuid()
        private set

    var source: FileCloneInputStream? = null
        private set

    var size: Long = 0
        private set

    var maxPartSize: Long = 0
        private set

    var minPartSize: Long = 0
        private set

    var maxPartCount: Long = 0
        private set

    fun partSize(partSize: Long): MultipartBuilder {
        this.partSize = partSize
        return this
    }

    fun id(id: String): MultipartBuilder {
        this.id = id
        return this
    }


    fun source(source: InputStream): MultipartBuilder {
        this.source = source as? FileCloneInputStream ?: FileCloneInputStream(source)
        return size(this.source!!.size())
    }


    fun source(file: File): MultipartBuilder {
        return source(FileCloneInputStream(file))
    }

    fun size(size: Long): MultipartBuilder {
        this.size = size
        return this
    }

    fun maxPartSize(maxPartSize: Long): MultipartBuilder {
        this.maxPartSize = maxPartSize
        return this
    }

    fun minPartSize(minPartSize: Long): MultipartBuilder {
        this.minPartSize = minPartSize
        return this
    }

    fun maxPartCount(maxPartCount: Long): MultipartBuilder {
        this.maxPartCount = maxPartCount
        return this
    }

    fun parts(): Collection<Part> {
        if (minPartSize > 0 && partSize < minPartSize) {
            partSize(minPartSize)
        }
        val number: Long = Multipart.calculate(size, partSize)
        // 限制了最大分片数量. 超过之后重新分配每片大小
        if (maxPartCount > 0 && number > maxPartCount) {
            partSize(partSize + (partSize / 2))
            return parts()
        }
        return Multipart.split(size, partSize)
    }

    fun build(): Multipart {
        val parts = parts()
        return Multipart(id, source?.source(), size, partSize, parts)
    }
}
