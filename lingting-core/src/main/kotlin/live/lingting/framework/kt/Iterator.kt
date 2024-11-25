package live.lingting.framework.kt

/**
 * @author lingting 2024/11/25 17:02
 */
fun <T> Iterator<T>.toList(): MutableList<T> {
    val list: MutableList<T> = ArrayList()
    while (hasNext()) {
        list.add(next())
    }
    return list
}

fun <T> Iterator<T>.toSet(): MutableSet<T> {
    val set: MutableSet<T> = HashSet()
    while (hasNext()) {
        set.add(next())
    }
    return set
}
