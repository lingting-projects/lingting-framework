package live.lingting.framework.mybatis.typehandler

import com.fasterxml.jackson.core.type.TypeReference
import live.lingting.framework.money.Money

class MoneySetTypeHandler : AbstractSetTypeHandler<Money>() {

    override val reference = object : TypeReference<Set<Money>>() {
    }

}
