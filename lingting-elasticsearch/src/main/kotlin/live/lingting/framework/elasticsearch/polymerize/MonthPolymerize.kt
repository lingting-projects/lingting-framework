package live.lingting.framework.elasticsearch.polymerize

import java.time.LocalDate
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAccessor
import live.lingting.framework.elasticsearch.IndexInfo

/**
 * @author lingting 2024/11/26 14:12
 */
class MonthPolymerize : Polymerize {

    override fun index(info: IndexInfo): String {
        if (info.polymerizeLimit < 1) {
            return info.matchIndex
        }
        val list = ArrayList<String>()
        val date = LocalDate.now()
        for (i in 0 until info.polymerizeLimit) {
            if (i > 0) {
                date.minusMonths(i)
            }
            val format = String.format("%s%s%d%s%02d", info.index, info.separate, date.year, info.separate, date.monthValue)
            list.add(format)
        }
        return list.joinToString(",")
    }

    override fun <T> index(info: IndexInfo, o: T): String {
        val cf = info.polymerizeFields[0]
        val v = cf.get(o as Any)
        requireNotNull(v) { "Elasticsearch polymerize field is null! cls: ${info.cls.simpleName}, field: ${cf.name}" }
        if (v !is TemporalAccessor) {
            throw IllegalArgumentException("Elasticsearch polymerize field is not TemporalAccessor! cls: ${info.cls.simpleName}, field: ${cf.name}")
        }
        val year = v.get(ChronoField.YEAR)
        val month = v.get(ChronoField.MONTH_OF_YEAR)
        return String.format("%s%s%d%s%02d", info.index, info.separate, year, info.separate, month)
    }
}
