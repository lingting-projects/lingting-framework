package live.lingting.framework.elasticsearch.builder

import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.elasticsearch.core.UpdateByQueryRequest
import java.util.function.Consumer
import live.lingting.framework.elasticsearch.retry.ElasticsearchRetryProperties

/**
 * @author lingting 2025/1/22 11:18
 */
open class UpdateBuilder<E> @JvmOverloads constructor(
    private var searchBuilder: SearchBuilder<E> = SearchBuilder<E>(),
    private var scriptBuilder: ScriptBuilder<E> = ScriptBuilder<E>()
) : Builder<UpdateByQueryRequest.Builder, UpdateBuilder<E>>,
    Compare<E, UpdateBuilder<E>>,
    Script<E, UpdateBuilder<E>> {

    fun all(all: Boolean): UpdateBuilder<E> {
        searchBuilder.all(all)
        return this
    }

    fun merge(builder: SearchBuilder<E>): UpdateBuilder<E> {
        searchBuilder.merge(builder)
        return this
    }

    fun buildSearch(): SearchBuilder<E> = searchBuilder.copy()

    protected var refresh: Boolean = false

    fun refresh(refresh: Boolean): UpdateBuilder<E> {
        this.refresh = refresh
        return this
    }

    var retry: ElasticsearchRetryProperties? = null
        protected set

    fun retry(retry: ElasticsearchRetryProperties): UpdateBuilder<E> {
        this.retry = retry
        return this
    }

    // region query

    override fun merge(builder: Compare<*, *>): UpdateBuilder<E> {
        searchBuilder.merge(builder)
        return this
    }

    override fun addMust(queries: Collection<Query>): UpdateBuilder<E> {
        searchBuilder.addMust(queries)
        return this
    }

    override fun addMustNot(queries: Collection<Query>): UpdateBuilder<E> {
        searchBuilder.addMustNot(queries)
        return this
    }

    override fun addShould(queries: Collection<Query>): UpdateBuilder<E> {
        searchBuilder.addShould(queries)
        return this
    }

    override fun buildQuery(): Query = searchBuilder.buildQuery()

    // endregion

    // region script
    override fun param(name: String, value: Any?): UpdateBuilder<E> {
        scriptBuilder.param(name, value)
        return this
    }

    override fun append(script: String): UpdateBuilder<E> {
        scriptBuilder.append(script)
        return this
    }

    override fun painless(): UpdateBuilder<E> {
        scriptBuilder.painless()
        return this
    }

    override fun lang(lang: String): UpdateBuilder<E> {
        scriptBuilder.lang(lang)
        return this
    }

    override fun set(field: String, value: Any?): UpdateBuilder<E> {
        scriptBuilder.set(field, value)
        return this
    }

    override fun setIfAbsent(field: String, value: Any?): UpdateBuilder<E> {
        scriptBuilder.setIfAbsent(field, value)
        return this
    }

    override fun buildScript(): co.elastic.clients.elasticsearch._types.Script {
        return scriptBuilder.buildScript()
    }

    // endregion

    private var customizer: Consumer<UpdateByQueryRequest.Builder>? = null

    override fun customizer(consumer: Consumer<UpdateByQueryRequest.Builder>): UpdateBuilder<E> {
        this.customizer = consumer
        return this
    }

    override fun copy(): UpdateBuilder<E> {
        return UpdateBuilder<E>().merge(this)
    }

    override fun merge(builder: UpdateBuilder<E>): UpdateBuilder<E> {
        searchBuilder.merge(builder.searchBuilder)
        return this
    }

    override fun build(): UpdateByQueryRequest.Builder {
        val builder = UpdateByQueryRequest.Builder()
        builder.query(buildQuery())
        builder.script(buildScript())
        builder.refresh(refresh)
        customizer?.accept(builder)
        return builder
    }

}
