package live.lingting.framework.elasticsearch.polymerize

import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.time.temporal.TemporalUnit
import java.util.concurrent.ConcurrentHashMap
import live.lingting.framework.elasticsearch.IndexInfo
import live.lingting.framework.time.DateTime

/**
 * @author lingting 2024/12/18 19:10
 */
abstract class DateTimePolymerize : Polymerize {

    companion object {
        @JvmField
        val CACHE = ConcurrentHashMap<DateTimePolymerize, ConcurrentHashMap<IndexInfo, DateTimeFormatter>>()
    }

    protected abstract fun formatter(info: IndexInfo): DateTimeFormatter

    abstract val unit: TemporalUnit

    fun from(info: IndexInfo): DateTimeFormatter {
        return CACHE.computeIfAbsent(this) { ConcurrentHashMap() }.computeIfAbsent(info) { formatter(info) }
    }

    override fun index(info: IndexInfo): String {
        if (info.polymerizeLimit < 1) {
            return info.matchIndex
        }
        val current = DateTime.current()
        return LinkedHashSet<String>().apply {

            for (i in 0 until info.polymerizeLimit) {
                val time = current.minus(i, unit)
                val index = index(info, time)
                add(index)
            }


        }.joinToString(",")
    }

    override fun <T> index(info: IndexInfo, o: T): String {
        val cf = info.polymerizeFields[0]
        val v = cf.get(o as Any)
        require(v is TemporalAccessor) { "Elasticsearch polymerize field is not TemporalAccessor! cls: ${info.cls.simpleName}, field: ${cf.name}. value: $v" }
        return index(info, v)
    }

    fun index(info: IndexInfo, ta: TemporalAccessor): String {
        val formatter = from(info)
        val format = formatter.format(ta)
        return "${info.index}${info.separate}$format"
    }


}
