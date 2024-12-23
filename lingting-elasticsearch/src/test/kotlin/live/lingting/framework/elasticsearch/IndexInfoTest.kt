package live.lingting.framework.elasticsearch

import live.lingting.framework.elasticsearch.annotation.Index
import live.lingting.framework.elasticsearch.polymerize.PolymerizeFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024/12/23 14:56
 */
class IndexInfoTest {
    val polymerizeFactory: PolymerizeFactory = PolymerizeFactory()

    @Test
    fun test() {
        val ei1 = IndexInfo.create(
            ElasticsearchProperties(),
            E::class.java,
            polymerizeFactory
        )
        assertEquals("e_index", ei1.index)

        val ei2 = IndexInfo.create(
            ElasticsearchProperties().apply {
                index.prefix = "prefix"
            },
            E::class.java,
            polymerizeFactory
        )
        assertEquals("prefix_e_index", ei2.index)

        val ef1 = IndexInfo.create(
            ElasticsearchProperties(),
            EF::class.java,
            polymerizeFactory
        )
        assertEquals("pf_ef_index", ef1.index)

        val ef2 = IndexInfo.create(
            ElasticsearchProperties().apply {
                index.prefix = "prefix"
            },
            EF::class.java,
            polymerizeFactory
        )
        assertEquals("prefix_pf_ef_index", ef2.index)
    }
}

@Index(index = "e_index")
class E {

}

@Index(prefix = "pf", index = "ef_index")
class EF {

}
