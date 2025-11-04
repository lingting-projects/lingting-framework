package live.lingting.framework.elasticsearch.builder

import live.lingting.framework.elasticsearch.EFunction
import live.lingting.framework.elasticsearch.builder.ScriptBuilder.Companion.genSymbol
import live.lingting.framework.elasticsearch.util.ElasticsearchUtils

/**
 * @author lingting 2025/1/22 13:36
 */
interface Script<T, B : Script<T, B>> {

    // region basic

    fun param(name: String, value: Any?): B

    fun append(script: String): B

    fun <R> append(script: String, func: EFunction<T, R>, value: R?): B {
        val field: String = ElasticsearchUtils.fieldName(func)
        return append(script, field, value)
    }

    fun append(script: String, name: String, value: Any?): B {
        if (value != null) {
            param(name, value)
        }
        return append(script)
    }

    fun lang(lang: String): B

    fun painless(): B

    // endregion

    // region set

    fun <R> set(func: EFunction<T, R>, value: R?): B {
        val field = ElasticsearchUtils.fieldName(func)
        return set(field, value)
    }

    fun set(field: String, value: Any?): B

    fun setIfAbsent(field: String, value: Any?): B

    fun increasing(field: String): B = increasing(field, 1)

    fun increasing(field: String, value: Number = 1): B {
        return append(genSymbol(field, "+="), field, value)
    }

    fun decrease(field: String): B = decrease(field, 1)

    fun decrease(field: String, value: Number = 1): B {
        return append(genSymbol(field, "-="), field, value)
    }

    // endregion

    fun buildScript(): co.elastic.clients.elasticsearch._types.Script

}
