package live.lingting.framework.value

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.Optional
import java.util.function.Supplier
import live.lingting.framework.function.ThrowingFunction
import live.lingting.framework.kt.logger
import live.lingting.framework.util.FileUtils
import live.lingting.framework.util.StreamUtils
import live.lingting.framework.util.StringUtils

/**
 * @author lingting 2023-05-23 09:12
 */
@Suppress("UNCHECKED_CAST")
abstract class AbstractFileValue<T> {
    protected val log = logger()

    protected val file: File

    protected var value: T? = null

    protected constructor(dir: File, filename: Any) {
        val filledFilename = fillFilename(filename)
        this.file = File(dir, filledFilename)
    }

    protected constructor(file: File) {
        this.file = file
    }

    /**
     * 文件名处理, 用于调整后缀
     *
     * @return 返回真实的文件名
     */
    abstract fun fillFilename(filename: Any): String

    protected abstract fun ofClass(str: String, cls: Class<T>): T?

    protected abstract fun toString(t: T): String

    fun optional(function: ThrowingFunction<String, T>): Optional<T> {
        if (!file.exists()) {
            return Optional.empty<T>() as Optional<T>
        }
        if (value != null) {
            return Optional.of(value!!) as Optional<T>
        }
        try {
            Files.newInputStream(file.toPath()).use { `in` ->
                val string = StreamUtils.toString(`in`)
                value = if (!StringUtils.hasText(string)) {
                    null
                } else {
                    function.apply(string)
                }
                return Optional.ofNullable(value) as Optional<T>
            }
        } catch (e: Throwable) {
            log.error("解析文件内容异常! 文件: {}", file.absolutePath, e)
            return Optional.empty<T>() as Optional<T>
        }
    }

    fun optional(cls: Class<T>): Optional<T> {
        return optional(ThrowingFunction { str -> ofClass(str, cls) })
    }

    fun set(t: T) {
        value = t
        FileUtils.createFile(file)
        Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8).use { writer ->
            val str = toString(t)
            writer.write(str)
        }
    }

    fun orElseGet(supplier: Supplier<T>, cls: Class<T>): T {
        return orElseGet(supplier) { optional(cls) }
    }

    fun orElseGet(supplier: Supplier<T>, optionalSupplier: Supplier<Optional<T>>): T {
        val optional = optionalSupplier.get()
        if (optional.isPresent) {
            return optional.get()
        }
        val t = supplier.get()
        set(t)
        return t
    }

}
