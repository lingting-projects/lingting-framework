package live.lingting.framework.mybatis.typehandler

import com.fasterxml.jackson.core.type.TypeReference
import live.lingting.framework.jackson.JacksonUtils
import live.lingting.framework.money.Money

/**
 * @author lingting 2023/1/3 15:28
 */
class MoneyListTypeHandler : AbstractListTypeHandler<Money?>() {
    override fun toObject(json: String?): List<Money> {
        return JacksonUtils.toObj(json, object : TypeReference<T?>() {
        })
    }
}
