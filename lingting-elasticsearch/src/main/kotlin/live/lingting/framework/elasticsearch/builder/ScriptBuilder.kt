package live.lingting.framework.elasticsearch.builder

import co.elastic.clients.elasticsearch._types.Script
import co.elastic.clients.json.JsonData
import live.lingting.framework.util.StringUtils

/**
 * @author lingting 2024-03-06 19:27
 */
open class ScriptBuilder<T> :
    live.lingting.framework.elasticsearch.builder.Script<T, ScriptBuilder<T>> {

    companion object {
        const val PREFIX_SOURCE: String = "ctx._source"

        const val PREFIX_PARAMS: String = "params"

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
            val condition = "$sourceField==null"
            val script = genSetParams(field)
            return genIf(condition, script)
        }

    }

    protected val sourceBuilder = StringBuilder()

    protected val params = HashMap<String, JsonData>()

    protected var lang = "painless"

    override fun param(name: String, value: Any?): ScriptBuilder<T> {
        params[name] = JsonData.of(value)
        return this
    }

    override fun append(script: String): ScriptBuilder<T> {
        if (!StringUtils.hasText(script)) {
            return this
        }

        if (sourceBuilder.isNotEmpty()) {
            sourceBuilder.append("\n")
        }
        sourceBuilder.append(fillEnd(script))
        return this
    }

    override fun lang(lang: String): ScriptBuilder<T> {
        this.lang = lang
        return this
    }

    override fun painless(): ScriptBuilder<T> {
        return lang("painless")
    }

    override fun set(field: String, value: Any?): ScriptBuilder<T> {
        val script = if (value == null) genSetNull(field) else genSetParams(field)
        return append(script, field, value)
    }

    override fun setIfAbsent(field: String, value: Any?): ScriptBuilder<T> {
        if (value != null) {
            val script = genSetIfAbsent(field)
            return append(script, field, value)
        }
        return this
    }

    override fun buildScript(): Script {
        return build()
    }

    fun build(): Script {
        val source = sourceBuilder.toString()
        return Script.of { it.lang(lang).source(source).params(HashMap(params)) }
    }

}
