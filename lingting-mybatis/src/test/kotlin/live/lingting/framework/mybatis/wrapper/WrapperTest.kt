package live.lingting.framework.mybatis.wrapper

import com.baomidou.mybatisplus.annotation.TableName
import com.baomidou.mybatisplus.core.MybatisConfiguration
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper
import live.lingting.framework.mybatis.wrapper.Wrappers.query
import org.apache.ibatis.builder.MapperBuilderAssistant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.function.Consumer

class WrapperTest {
    @BeforeEach
    fun before() {
        TableInfoHelper.initTableInfo(MapperBuilderAssistant(MybatisConfiguration(), ""), Table::class.java)
    }

    @Test
    fun test() {
        val query = query<Any>()
            .alias("t")
            .select("s1", "s2", "s3")
            .eq("q1", "v1")
            .`in`("i1", "iv1", "iv2")
            .between("b1", "bv1", "bv2")
            .and(Consumer { m -> m.eq("aq1", "aqv1").and(Consumer { mm -> mm.eq("aaq1", "aav1") }) })
            .and(Consumer { m -> m.eq("oq1", "oqv1").or(Consumer { mm: QueryWrapper<Any> -> mm.eq("ooq1", "oov1") }) })
            .groupBy("g1", "g2")
            .orderByDesc("od1", "od2")
            .orderByAsc("oa1", "oa2")
            .having("sum(t.h1) > 0")

            .limitOne()

        assertEquals("t.s2, t.s1, t.s3", query.getSqlSelect())
        assertEquals(
            "(t.q1 = #{ew.params.PARAM_2_1} AND t.i1 IN (#{ew.params.PARAM_2_2},#{ew.params.PARAM_2_3}) AND t.b1 BETWEEN #{ew.params.PARAM_2_4} AND #{ew.params.PARAM_2_5} AND (aq1 = #{ew.params.PARAM_3_6} AND (aaq1 = #{ew.params.PARAM_4_7})   )    AND (oq1 = #{ew.params.PARAM_5_8} OR (ooq1 = #{ew.params.PARAM_6_9})   )   ) GROUP BY t.g1, t.g2 HAVING sum(t.h1) > 0 ORDER BY t.od1 DESC, t.od2 DESC,t.oa1 ASC, t.oa2 ASC  limit 1",
            query.getSqlSegment()
        )
    }

    @Test
    fun testSafe() {
        val query: QueryWrapper<Table> = query<Table>(Table::class.java)
            .alias("t1")
            .eq("e1", "ev1")
            .`in`<Table>("i1",
                Consumer { queryWrapper ->
                    queryWrapper.cls(Table::class.java)
                        .alias("t2")
                        .select("t1_id")
                        .`in`("o2i1", "o2iv1", "o2iv2")
                })

        assertEquals(
            "SELECT * FROM table t1 WHERE (t1.e1 = #{ew.params.PARAM_0_1} AND t1.i1 IN (SELECT t2.t1_id FROM table t2 WHERE (t2.o2i1 IN (#{ew.params.PARAM_1_2},#{ew.params.PARAM_1_3}))))",
            query.getSafeSql()
        )
    }

    @Test
    fun testLambda() {
        val query: QueryWrapper<Table> = query<Table>(Table::class.java)
            .eq(Table::id, "")
            .eqIfPresent(Table::id, "")
            .eqIfPresent(Table::name, null)

        assertEquals(
            "SELECT * FROM table WHERE (id = #{ew.params.PARAM_7_1})",
            query.getSafeSql()
        )
    }

    @TableName("table")
    class Table {
        var id: String? = null
        var name: String? = null
    }
}
