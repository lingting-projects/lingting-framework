package live.lingting.framework.elasticsearch.polymerize

import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import live.lingting.framework.elasticsearch.IndexInfo

/**
 * @author lingting 2024/11/26 14:12
 */
class DayPolymerize : DateTimePolymerize() {

    override fun formatter(info: IndexInfo): DateTimeFormatter {
        return DateTimeFormatter.ofPattern("yyyy'${info.separate}'MM'${info.separate}'dd")
    }

    override val unit: TemporalUnit = ChronoUnit.DAYS

}
