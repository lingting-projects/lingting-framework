package live.lingting.framework.elasticsearch.polymerize

import java.time.LocalDateTime
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

    open fun from(info: IndexInfo): DateTimeFormatter {
        return CACHE.computeIfAbsent(this) { ConcurrentHashMap() }.computeIfAbsent(info) { formatter(info) }
    }

    override fun index(info: IndexInfo): String {
        if (info.polymerizeLimit < 1) {
            return info.matchIndex
        }
        return indices(info).joinToString(",")
    }

    override fun <T> index(info: IndexInfo, o: T): String {
        val cf = info.polymerizeFields[0]
        val v = cf.get(o as Any)
        require(v is TemporalAccessor) { "Elasticsearch polymerize field is not TemporalAccessor! cls: ${info.cls.simpleName}, field: ${cf.name}. value: $v" }
        return index(info, v)
    }

    open fun index(info: IndexInfo, ta: TemporalAccessor): String {
        val formatter = from(info)
        val format = formatter.format(ta)
        return "${info.index}${info.separate}$format"
    }

    open fun indices(info: IndexInfo): LinkedHashSet<String> {
        val current = DateTime.current()
        return indices(current, info)
    }

    open fun indices(current: LocalDateTime, info: IndexInfo): LinkedHashSet<String> {
        var before = info.polymerizeLimit
        var after = 0L

        if (info.polymerizeSplit) {
            after = info.polymerizeLimit / 2
            before = info.polymerizeLimit - after
        }
        return indices(current, info, before, after)
    }

    open fun indices(current: LocalDateTime, info: IndexInfo, before: Long, after: Long): LinkedHashSet<String> {
        val set = LinkedHashSet<String>()
        for (i in 0 until before + 1) {
            val time = current.minus(i, unit)
            val index = index(info, time)
            set.add(index)
        }
        for (i in 0 until after + 1) {
            val time = current.plus(i, unit)
            val index = index(info, time)
            set.add(index)
        }
        return set
    }

}
