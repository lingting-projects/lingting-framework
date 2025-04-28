package live.lingting.framework.multipart

import live.lingting.framework.data.DataSize

/**
 * 分片详情
 * 字节范围为全包. 从第 [start] 位到第 [end] 个字节
 * @author lingting 2024-09-05 14:47
 */
data class Part(val index: Long, val start: DataSize, val end: DataSize) {

    val size = end - start + 1

}
