package live.lingting.framework.jackson.value

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import java.io.File
import java.util.Optional
import java.util.function.Supplier
import live.lingting.framework.function.ThrowingFunction
import live.lingting.framework.jackson.JacksonUtils
import live.lingting.framework.util.StringUtils
import live.lingting.framework.value.AbstractFileValue

/**
 * @author lingting 2023-06-05 09:46
 */
class FileJsonValue<T> : AbstractFileValue<T> {
    constructor(dir: File, filename: Any) : super(dir, filename)

    constructor(file: File) : super(file)

    override fun fillFilename(filename: Any): String {
        if (filename is String && filename.endsWith(SUFFIX)) {
            return filename
        }
        return String.format("%s%s", filename, SUFFIX)
    }

    protected fun of(str: String, function: ThrowingFunction<String, T>): T? {
        if (StringUtils.hasText(str)) {
            return function.apply(str)
        }
        return null
    }

    override fun ofClass(json: String, cls: Class<T>): T? {
        return of(json, ThrowingFunction<String, T> { s -> mapper.readValue(s, cls) })
    }

    override fun toString(t: T): String {
        return mapper.writeValueAsString(t)
    }

    fun optional(reference: TypeReference<T>): Optional<T> {
        return optional(ThrowingFunction<String, T> { json -> of(json, ThrowingFunction<String, T> { s -> mapper.readValue(s, reference) }) })
    }

    fun orElseGet(supplier: Supplier<T>, reference: TypeReference<T>): T {
        return orElseGet(supplier) { optional(reference) }
    }

    companion object {
        const val SUFFIX: String = ".json"

        val mapper: ObjectMapper = JacksonUtils.mapper.copy()

        init {
            mapper.enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
}
