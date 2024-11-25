package live.lingting.framework.value.unique

import live.lingting.framework.id.Snowflake
import live.lingting.framework.value.UniqueValue

/**
 * @author lingting 2024/11/25 16:49
 */
class SnowflakeUniqueValue(val snowflake: Snowflake = Snowflake(0, 0)) : UniqueValue<Long> {

    override fun next(): Long = snowflake.nextId()

    override fun batch(count: Int): List<Long> = snowflake.nextIds(count)

}
