package live.lingting.framework.elasticsearch.builder

import java.util.function.Consumer

/**
 * @author lingting 2025/1/21 17:49
 */
interface Builder<R, B : Builder<R, B>> {

    fun copy(): B

    fun merge(builder: B): B

    fun customizer(consumer: Consumer<R>): B

    fun build(): R

}
