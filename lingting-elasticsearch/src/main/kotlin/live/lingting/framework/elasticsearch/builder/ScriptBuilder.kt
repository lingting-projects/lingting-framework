package live.lingting.framework.elasticsearch.builder

import co.elastic.clients.elasticsearch._types.Script
import co.elastic.clients.json.JsonData
import live.lingting.framework.elasticsearch.ElasticsearchFunction
import live.lingting.framework.elasticsearch.ElasticsearchUtils
import live.lingting.framework.util.StringUtils

/**
 * @author lingting 2024-03-06 19:27
 */
class ScriptBuilder<T> {
    private val sourceBuilder = StringBuilder()

    private val params: MutableMap<String, JsonData> = HashMap()

    private var lang = "painless"

    // region params
    fun <R> put(func: ElasticsearchFunction<T, R>, value: R): ScriptBuilder<T> {
        val field: String = ElasticsearchUtils.fieldName(func)
        return put(field, value)
    }

    fun <R> put(name: String, value: R): ScriptBuilder<T> {
        val data = JsonData.of(value)
        params[name] = data
        return this
    }

    fun append(script: String): ScriptBuilder<T> {
        if (!StringUtils.hasText(script)) {
            return this
        }

        if (!sourceBuilder.isEmpty()) {
            sourceBuilder.append("\n")
        }
        sourceBuilder.append(fillEnd(script))
        return this
    }

    fun <R> append(script: String, field: String, value: R): ScriptBuilder<T> {
        if (value != null) {
            params[field] = JsonData.of(value)
        }
        return append(script)
    }

    fun <R> set(func: ElasticsearchFunction<T, R>, value: R): ScriptBuilder<T> {
        val field: String = ElasticsearchUtils.fieldName(func)
        return set(field, value)
    }

    fun <R> set(field: String, value: R): ScriptBuilder<T> {
        val script = if (value == null) genSetNull(field) else genSetParams(field)
        return append(script, field, value)
    }

    fun setIfAbsent(field: String, value: Any): ScriptBuilder<T> {
        if (value != null) {
            val script = genSetIfAbsent(field)
            return append(script, field, value)
        }
        return this
    }

    fun lang(lang: String): ScriptBuilder<T> {
        this.lang = lang
        return this
    }

    fun painless(): ScriptBuilder<T> {
        return lang("painless")
    }

    // endregion

    // region script number
    @JvmOverloads
    fun increasing(field: String, value: Number = 1): ScriptBuilder<T> {
        return append(genSymbol(field, "+="), field, value)
    }

    @JvmOverloads
    fun decrease(field: String, value: Number = 1): ScriptBuilder<T> {
        return append(genSymbol(field, "-="), field, value)
    }

    // endregion

    // region build
    fun build(): Script {
        val source = sourceBuilder.toString()
        return Script.of { s -> s.lang(lang).source(source).params(HashMap(params)) }
    } // endregion

    companion object {
        const val PREFIX_SOURCE: String = "ctx._source"

        const val PREFIX_PARAMS: String = "params"

        // endregion
        // region script basic
        @JvmStatic
        fun <T> builder(): ScriptBuilder<T> {
            return ScriptBuilder()
        }

        @JvmStatic
        fun fillEnd(source: String): String {
            if (!source.endsWith(";")) {
                return "$source;"
            }
            return source
        }

        @JvmStatic
        fun genSourceField(field: String): String {
            return "$PREFIX_SOURCE.$field"
        }

        @JvmStatic
        fun genParamsField(field: String): String {
            return "$PREFIX_PARAMS.$field"
        }

        @JvmOverloads
        @JvmStatic
        fun genSymbol(field: String, symbol: String, value: String = genParamsField(field)): String {
            val text = genSourceField(field)
            return "$text $symbol $value"
        }

        @JvmStatic
        fun genIf(condition: String, script: String): String {
            val end = fillEnd(script)
            return "if($condition){$end}"
        }

        @JvmStatic
        fun genSetNull(field: String): String {
            return genSymbol(field, "=", "null")
        }

        @JvmStatic
        fun genSetParams(field: String): String {
            return genSymbol(field, "=", genParamsField(field))
        }

        @JvmStatic
        fun genSetIfAbsent(field: String): String {
            val sourceField = genSourceField(field)
            val condition = "$sourceField==null || $sourceField==''"
            val script = genSetParams(field)
            return genIf(condition, script)
        }
    }
}
