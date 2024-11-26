package live.lingting.framework.elasticsearch

import java.io.Serializable
import java.util.function.Function

/**
 * @author lingting 2023-07-13 19:16
 */
interface EFunction<T, R> : Function<T, R>, Serializable
