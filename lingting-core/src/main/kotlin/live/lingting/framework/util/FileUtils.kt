package live.lingting.framework.util

import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.nio.file.CopyOption
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import live.lingting.framework.kt.logger
import org.slf4j.Logger


/**
 * @author lingting
 */
object FileUtils {
    @JvmField
    val TEMP_DIR: File = SystemUtils.tmpDirLingting()
    private val log: Logger = logger()

    /**
     * 扫描指定路径下所有文件
     * @param path      指定路径
     * @param recursive 是否递归
     * @return java.util.List<java.lang.String>
    </java.lang.String> */
    @JvmStatic
    fun scanFile(path: String, recursive: Boolean): List<String> {
        val list: MutableList<String> = ArrayList()
        val file = File(path)
        if (!file.exists()) {
            return list
        }

        if (file.isFile) {
            list.add(file.absolutePath)
            return list
        }

        // 文件夹
        val files = file.listFiles()
        if (ArrayUtils.isEmpty<File>(files)) {
            return list
        }

        for (childFile in files!!) {
            // 如果递归
            if (recursive && childFile.isDirectory) {
                list.addAll(scanFile(childFile.absolutePath, true))
            } else if (childFile.isFile) {
                list.add(childFile.absolutePath)
            }
        }

        return list
    }

    /**
     * 创建指定文件夹, 已存在时不会重新创建
     * @param dir 文件夹.
     */
    @JvmStatic
    fun createDir(dir: File): Boolean {
        return dir.exists() || dir.mkdirs() || dir.exists()
    }

    @JvmStatic
    fun createTempDir(child: String): File {
        val file = File(TEMP_DIR, child)
        createDir(file)
        return file
    }

    /**
     * 创建指定文件, 已存在时不会重新创建
     * @param file 文件.
     */
    @JvmStatic
    fun createFile(file: File): Boolean {
        if (file.exists()) {
            return true
        }

        if (!createDir(file.parentFile)) {
            return false
        }

        try {
            return file.createNewFile()
        } catch (e: IOException) {
            log.debug("file create error! path: {}", file.absolutePath, e)
            return false
        }
    }

    @JvmStatic
    fun createFile(filename: String, dir: File): File {
        if (!createDir(dir)) {
            throw IOException("dir create error! path : " + dir.absolutePath)
        }
        val file = File(dir, filename)
        if (createFile(file)) {
            return file
        }
        throw IOException("file create error! path : " + file.absolutePath)
    }

    /**
     * 创建临时文件
     * @param suffix 文件特征
     * @param dir    文件存放位置
     * @return 临时文件对象
     */
    @JvmStatic
    @JvmOverloads
    fun createTemp(suffix: String = ".tmp", dir: File = TEMP_DIR): File {
        if (!createDir(dir)) {
            throw IOException("temp dir create error! path : " + dir.absolutePath)
        }
        return File.createTempFile("lingting.", suffix, dir)
    }

    @JvmStatic
    fun createTemp(`in`: InputStream): File {
        val file = createTemp()
        StreamUtils.write(`in`, file)
        return file
    }

    @JvmStatic
    fun createTemp(`in`: InputStream, suffix: String): File {
        val file = createTemp(suffix)
        StreamUtils.write(`in`, file)
        return file
    }

    @JvmStatic
    fun createTemp(`in`: InputStream, suffix: String, dir: File): File {
        val file = createTemp(suffix, dir)
        StreamUtils.write(`in`, file)
        return file
    }

    /**
     * 复制文件
     * @param source   源文件
     * @param target   目标文件
     * @param override 如果目标文件已存在是否覆盖
     * @param options  其他文件复制选项 [StandardCopyOption]
     * @return 目标文件地址
     */
    @JvmStatic
    fun copy(source: File, target: File, override: Boolean, vararg options: CopyOption): Path {
        val list: MutableList<CopyOption> = ArrayList()
        if (override) {
            list.add(StandardCopyOption.REPLACE_EXISTING)
        }

        list.addAll(options)

        return Files.copy(source.toPath(), target.toPath(), *list.toTypedArray<CopyOption>())
    }

    @JvmStatic
    fun write(file: File, `in`: InputStream) {
        if (!createFile(file)) {
            throw FileNotFoundException("path: " + file.absolutePath)
        }
        Files.newOutputStream(file.toPath()).use { out ->
            StreamUtils.write(`in`, out)
        }
    }

    @JvmStatic
    fun delete(file: File?): Boolean {
        try {
            if (file != null) {
                Files.delete(file.toPath())
            }
            return true
        } catch (e: IOException) {
            return false
        }
    }


    /**
     * 依据系统文件路径分隔符解析
     */
    @JvmStatic
    fun getFilename(string: String): String {
        val path = Paths.get(string)
        return path.fileName.toString()
    }

    @JvmStatic
    fun getFilenameByUrl(url: String): String {
        return getFilename(url, "/")
    }

    /**
     * 依据指定分隔符解析
     */
    @JvmStatic
    fun getFilename(path: String, delimiter: String): String {
        if (!StringUtils.hasText(path)) {
            return ""
        }
        val split: Array<String> = path.split(delimiter.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return split[split.size - 1]
    }

    /**
     * 获取文件扩展名
     * @return java.lang.String eg: java
     */
    @JvmStatic
    fun getFileExt(filename: String): String {
        return getFileExt(filename, "\\.")
    }

    @JvmStatic
    fun getFileExt(filename: String, delimiter: String): String {
        if (!StringUtils.hasText(filename)) {
            return ""
        }
        val split: Array<String> = filename.split(delimiter.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return split[split.size - 1]
    }
}

