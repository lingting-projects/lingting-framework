package live.lingting.framework.mybatis.typehandler

import com.fasterxml.jackson.core.type.TypeReference
import live.lingting.framework.jackson.JacksonUtils
import live.lingting.framework.money.Money

class MoneySetTypeHandler : AbstractSetTypeHandler<Money?>() {
    override fun toObject(json: String?): Set<Money> {
        return JacksonUtils.toObj(json, object : TypeReference<T?>() {
        })
    }
}
