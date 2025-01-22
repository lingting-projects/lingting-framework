package live.lingting.framework.elasticsearch.builder

import co.elastic.clients.elasticsearch.core.search.SourceConfig

/**
 * @author lingting 2025/1/21 17:33
 */
open class DefaultSource<E> : Source<E, DefaultSource<E>> {

    protected var source: SourceConfig? = null

    override fun source(source: SourceConfig?): DefaultSource<E> {
        this.source = source
        return this
    }

    override fun buildSource(): SourceConfig? {
        return source
    }

}
